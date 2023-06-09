package com.pawka.trellocloneapp.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pawka.trellocloneapp.domain.user.User
import com.pawka.trellocloneapp.domain.user.UserRepository

object UserRepositoryImpl : UserRepository {

    private val currentUserLiveData = MutableLiveData<User?>()
    private val assignedMembersListLiveData = MutableLiveData<ArrayList<User>>()

    private const val USERS: String = "users"
    private const val ID: String = "id"
    private const val EMAIL: String = "email"
    private const val IMAGE: String = "image"
    private const val PASSWORD: String = "password"
    private const val NAME: String = "name"

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private val userFireStoreHandler = UserFireStoreHandler()

    override fun signUpUser(
        name: String,
        login: String,
        password: String,
        callback: (isSignUp: Boolean) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        val currentUser = User(it.uid, name, login)
                        userFireStoreHandler.writeUserToDatabase(currentUser, callback)
                    }
                } else {
                    Log.d("Register", "Registration Failed\n")
                    callback(false)
                }
            }
    }

    override fun signInUser(
        email: String,
        password: String,
        callback: (isSignIn: Boolean) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
                Log.d("Login", "Login Failed\n")
            }
    }

    override fun signOutUser() {
        auth.signOut()
        currentUserLiveData.value = null
    }

    override fun getCurrentUserData(): MutableLiveData<User?> {
        userFireStoreHandler.loadUserData()
        return currentUserLiveData
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun getCurrentFirebaseUser(): FirebaseUser? {
        userFireStoreHandler.loadUserData()
        return auth.currentUser
    }

    override fun getAssignedMembersList(
        assignedTo: ArrayList<String>,
        callback: () -> Unit
    ): MutableLiveData<ArrayList<User>> {
        userFireStoreHandler.getAssignedMembersList(assignedTo, callback)
        return assignedMembersListLiveData
    }

    override fun getMemberDetails(email: String, callback: (User) -> Unit) {
        userFireStoreHandler.getMemberDetails(email, callback)
    }

    override fun editUserData(name: String, login: String, password: String) {

    }

    override fun setProfileImage(uri: Uri, callback: () -> Unit) {
        storage.child("user_image").child(getCurrentUserId() ?: "").putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                    Log.e("Downloadable Image URL", uri.toString())
                    val userHashMap = HashMap<String, Any>()
                    userHashMap[IMAGE] = url.toString()
                    userFireStoreHandler.updateUserProfileData(userHashMap, callback)
                }
            }
    }

    class UserFireStoreHandler {

        private val db = FirebaseFirestore.getInstance().collection(USERS)
        fun writeUserToDatabase(userInfo: User, callback: (isAuth: Boolean) -> Unit) {
            val currentUserId = getCurrentUserId()
            currentUserId?.let { id ->
                db.document(id)
                    .set(userInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        Log.d("Register", "writeUserToDatabase failed\n ${it.message}")
                        callback(false)
                    }
            }
        }

        fun updateUserProfileData(userHashMap: HashMap<String, Any>, callback: () -> Unit) {
            val currentUserId = getCurrentUserId()
            currentUserId?.let { id ->
                db.document(id)
                    .update(userHashMap)
                    .addOnSuccessListener {
                        loadUserData()
                        callback()
                    }
            }
        }

        fun loadUserData() {
            val currentUserId = getCurrentUserId()
            currentUserId?.let { id ->
                db.document(id)
                    .get()
                    .addOnSuccessListener { document ->
                        val loggedInUser = document.toObject(User::class.java)!!
                        currentUserLiveData.value = loggedInUser
                    }
                    .addOnFailureListener {
                        Log.d("Register", "loadUserData() failed\n ${it.message}")
                    }
            }
        }

        fun getAssignedMembersList(assignedTo: ArrayList<String>, callback: () -> Unit) {
            db.whereIn(ID, assignedTo)
                .get()
                .addOnSuccessListener { document ->
                    val usersList: ArrayList<User> = ArrayList()

                    for (i in document.documents) {
                        val user = i.toObject(User::class.java)!!
                        usersList.add(user)
                    }

                    assignedMembersListLiveData.value = usersList
                    callback()
                }
        }

        fun getMemberDetails(email: String, callback: (User) -> Unit) {
            db.whereEqualTo(EMAIL, email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.documents.size > 0) {
                        val user = document.documents[0].toObject(User::class.java)!!
                        callback(user)
                    } else {
                        callback(User())
                    }
                }
        }
    }
}
