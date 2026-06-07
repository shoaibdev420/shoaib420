package com.example.ludostealth
import com.example.ludostealth.chat.SettingsScreen
import com.example.ludostealth.chat.ProfileScreen
import com.example.ludostealth.chat.ChatScreen
import com.example.ludostealth.chat.NewChatScreen
import com.example.ludostealth.chat.NewContact
import com.example.ludostealth.logic.MenuMusic
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ludostealth.chat.HiddenChatScreen
import com.example.ludostealth.chat.OtpVerificationScreen
import com.example.ludostealth.chat.PhoneAuthManager
import com.example.ludostealth.chat.ProfileSetupScreen
import com.example.ludostealth.chat.WhatsAppHomeScreen
import com.example.ludostealth.logic.GameBoardScreen
import com.example.ludostealth.screens.ComputerSetup
import com.example.ludostealth.screens.LocalSetup
import com.example.ludostealth.screens.Mode
import com.example.ludostealth.screens.Splash
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private enum class Screen {
        SPLASH,
        MODE,
        LOCAL_SETUP,
        COMPUTER_SETUP,
        LOCAL_PLAY,
        HIDDEN_CHAT,
        OTP_VERIFICATION,
        PROFILE_SETUP,
        WHATSAPP_HOME,
        NEW_CHAT,
        NEW_CONTACT,
        CHAT_SCREEN,
        SETTINGS,
        PROFILE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var screen by remember { mutableStateOf(Screen.SPLASH) }
            var localPlayersCount by remember { mutableStateOf(2) }
            var previousScreen by remember { mutableStateOf(Screen.MODE) }
            var enteredPhoneNumber by remember { mutableStateOf("") }
            var selectedContact by remember { mutableStateOf<Map<String, String>?>(null) }

            val phoneAuthManager = remember { PhoneAuthManager(this@MainActivity) }
            val auth = remember { FirebaseAuth.getInstance() }
            val firestore = remember { FirebaseFirestore.getInstance() }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            suspend fun openHiddenChatFlow(fromScreen: Screen) {
                previousScreen = fromScreen
                MenuMusic.stop()

                val currentUser = auth.currentUser

                // Agar login hi nahi hai -> pehle hidden chat / phone auth flow
                if (currentUser == null) {
                    screen = Screen.HIDDEN_CHAT
                    return
                }

                // Agar user logged in hai -> Firestore me profileCompleted check karo
                try {
                    val snapshot = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    val profileCompleted = snapshot.getBoolean("profileCompleted") ?: false

                    screen = if (profileCompleted) {
                        Screen.WHATSAPP_HOME
                    } else {
                        Screen.PROFILE_SETUP
                    }
                } catch (e: Exception) {
                    // Agar Firestore check fail ho jaye to safe fallback
                    screen = Screen.HIDDEN_CHAT
                }
            }

            when (screen) {

                Screen.SPLASH -> {
                    Splash(
                        onFinish = {
                            screen = Screen.MODE
                        }
                    )
                }

                Screen.MODE -> {
                    Mode(
                        onComputerClick = {
                            screen = Screen.COMPUTER_SETUP
                        },
                        onLocalClick = {
                            screen = Screen.LOCAL_SETUP
                        },
                        onHiddenTapSuccess = {
                            scope.launch {
                                openHiddenChatFlow(Screen.MODE)
                            }
                        }
                    )
                }

                Screen.LOCAL_SETUP -> {
                    LocalSetup(
                        onBack = { screen = Screen.MODE },
                        onPlay = { players, green, red, yellow, blue ->
                            localPlayersCount = players
                            screen = Screen.LOCAL_PLAY
                        },
                        onHiddenTapSuccess = {
                            scope.launch {
                                openHiddenChatFlow(Screen.LOCAL_SETUP)
                            }
                        }
                    )
                }

                Screen.LOCAL_PLAY -> {
                    GameBoardScreen(
                        playersCount = localPlayersCount,
                        onBack = { screen = Screen.LOCAL_SETUP },
                        onSettings = { /* later */ },
                        onHiddenTapSuccess = {
                            scope.launch {
                                openHiddenChatFlow(Screen.LOCAL_PLAY)
                            }
                        }
                    )
                }

                Screen.COMPUTER_SETUP -> {
                    ComputerSetup(
                        onBack = { screen = Screen.MODE },
                        onPlay = { players, color ->
                            // later add computer game flow
                        }
                    )
                }

                Screen.HIDDEN_CHAT -> {
                    HiddenChatScreen(
                        onClose = {
                            MenuMusic.start(context)
                            screen = previousScreen
                        },
                        onSendOtp = { fullPhoneNumber ->
                            enteredPhoneNumber = fullPhoneNumber

                            phoneAuthManager.sendOtp(
                                phoneNumber = fullPhoneNumber,
                                onCodeSent = {
                                    screen = Screen.OTP_VERIFICATION
                                },
                                onVerificationSuccess = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Phone verified successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    screen = Screen.PROFILE_SETUP
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    )
                }

                Screen.OTP_VERIFICATION -> {
                    OtpVerificationScreen(
                        phoneNumber = enteredPhoneNumber,
                        onClose = {
                            screen = previousScreen
                        },
                        onVerifyOtp = { code ->
                            phoneAuthManager.verifyOtp(
                                code = code,
                                onSuccess = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "OTP verified successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // OTP verify hone ke baad check karo
                                    val currentUser = auth.currentUser
                                    if (currentUser == null) {
                                        screen = Screen.HIDDEN_CHAT
                                    } else {
                                        scope.launch {
                                            try {
                                                val snapshot = firestore.collection("users")
                                                    .document(currentUser.uid)
                                                    .get()
                                                    .await()

                                                val profileCompleted =
                                                    snapshot.getBoolean("profileCompleted") ?: false

                                                screen = if (profileCompleted) {
                                                    Screen.WHATSAPP_HOME
                                                } else {
                                                    Screen.PROFILE_SETUP
                                                }
                                            } catch (e: Exception) {
                                                screen = Screen.PROFILE_SETUP
                                            }
                                        }
                                    }
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    )
                }

                Screen.PROFILE_SETUP -> {
                    ProfileSetupScreen(
                        onClose = {
                            screen = previousScreen
                        },
                        onProfileSaved = {
                            screen = Screen.WHATSAPP_HOME
                        }
                    )
                }

                Screen.WHATSAPP_HOME -> {
                    WhatsAppHomeScreen(
                        onOpenCamera = {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivity(intent)
                        },

                        onBack = {
                            MenuMusic.start(context)
                            screen = previousScreen
                        },

                        onSendMessageClick = {
                            screen = Screen.NEW_CHAT
                        },

                        onOpenSettings = {
                            screen = Screen.SETTINGS
                        },
                        onChatClick = { chat ->

                            selectedContact = chat
                            screen = Screen.CHAT_SCREEN
                        }
                    )
                }

                Screen.NEW_CHAT -> {
                    NewChatScreen(
                        onBack = {
                            screen = Screen.WHATSAPP_HOME
                        },
                        onNewGroupClick = {
                            Toast.makeText(
                                this@MainActivity,
                                "New group later",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onNewContactClick = {
                            screen = Screen.NEW_CONTACT
                        },
                        onNewCommunityClick = {
                            Toast.makeText(
                                this@MainActivity,
                                "New community later",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onContactClick = { contact ->   // ✅ ADD THIS
                            selectedContact = contact
                            screen = Screen.CHAT_SCREEN
                        }
                    )
                }

                Screen.NEW_CONTACT -> {
                    NewContact(
                        onBack = {
                            screen = Screen.NEW_CHAT
                        },
                        onSaveSuccess = {
                            screen = Screen.NEW_CHAT
                        }
                    )
                }

                Screen.CHAT_SCREEN -> {

                    ChatScreen(
                        name = selectedContact?.get("firstName")
                            ?: selectedContact?.get("name")
                            ?: "User",
                        receiverId = selectedContact?.get("uid") ?: "",

                        onBack = {
                            screen = Screen.WHATSAPP_HOME
                        }
                    )
                }
                Screen.SETTINGS -> {
                    SettingsScreen(
                        onBack = {
                            screen = Screen.WHATSAPP_HOME
                        },

                        onProfileClick = {
                            screen = Screen.PROFILE
                        }
                    )
                }

                Screen.PROFILE -> {
                    ProfileScreen(
                        onBack = {
                            screen = Screen.SETTINGS
                        }
                    )
                }

            }
        }
    }
}