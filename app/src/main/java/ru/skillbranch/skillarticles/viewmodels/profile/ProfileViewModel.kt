package ru.skillbranch.skillarticles.viewmodels.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.skillbranch.skillarticles.data.repositories.ProfileRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.Event
import ru.skillbranch.skillarticles.viewmodels.base.EventObserver
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.io.InputStream

class ProfileViewModel(handle: SavedStateHandle) :
        BaseViewModel<ProfileState>(handle, ProfileState()) {

    private val repository = ProfileRepository
    private val activityResults = MutableLiveData<Event<PendingAction>>()
    private val storagePermissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    init {
        subscribeOnDataSource(repository.getProfile()) { profile, state ->
            profile ?: return@subscribeOnDataSource null
            state.copy(
                    name = profile.name,
                    avatar = profile.avatar,
                    rating = profile.rating,
                    respect = profile.respect,
                    about = profile.about
            )
        }
    }

    fun handleTestAction(source: Uri, destination: Uri) {
        val pendingAction = PendingAction.EditAction(source to destination)
        updateState { it.copy(pendingAction = pendingAction) }
        requestPermissions(storagePermissions)
    }

    fun handlePermission(result: Map<String, Pair<Boolean, Boolean>>) {
        val isAllGranted =
                result.values
                    .map { it.first }
                    .all { true }
        val isAllMayBeShown =
                result.values
                    .map { it.second }
                    .all { true }

        when {
            isAllGranted -> executePendingAction()
            !isAllMayBeShown -> executeOpenSettings()
            else -> {
                val msgNeedStoragePermissions =
                        Notify.ErrorMessage(
                                "Need permissions for storage",
                                "Retry"
                        ) { requestPermissions(storagePermissions) }

                notify(content = msgNeedStoragePermissions)
            }
        }
    }

    private fun startForResult(action: PendingAction) {
        activityResults.value = Event(action)
    }

    private fun executeOpenSettings() {
        val errHandler = {
            val intentAppSettings =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:ru.skillbranch.skillarticles")
                    }

            startForResult(PendingAction.SettingsAction(intentAppSettings))
        }

        val msgNeedStoragePermissions =
                Notify.ErrorMessage(
                        "Need permissions for storage",
                        "Retry",
                        errHandler
                )

        notify(content = msgNeedStoragePermissions)
    }

    private fun executePendingAction() {
        val pendingAction = currentState.pendingAction ?: return
        startForResult(pendingAction)
    }

    fun handleUploadPhoto(inputStream: InputStream?) {
        inputStream ?: return // or show error notification

        launchSafety(
                compHandler = { updateState { it.copy(pendingAction = null) } }
        ) {
            val byteArray = withContext(Dispatchers.IO) { inputStream.use { input -> input.readBytes() } }
            val mediaType = "image/jpeg".toMediaType()
            val reqFile: RequestBody = byteArray.toRequestBody(mediaType)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("avatar", "name.jpg", reqFile)
            repository.uploadAvatar(body)
        }
    }

    fun observeActivityResults(owner: LifecycleOwner, handle: (action: PendingAction) -> Unit) {
        activityResults.observe(owner, EventObserver { handle(it) })
    }

    fun handleEditAction(source: Uri, destination: Uri) {
        updateState { it.copy(pendingAction = PendingAction.EditAction(source to destination)) }
        requestPermissions(storagePermissions)
    }

    fun handleDeleteAction() {
        // todo remove avatar on server
    }

    fun handleGalleryAction() {
        updateState { it.copy(pendingAction = PendingAction.GalleryAction("image/jpeg")) }
        requestPermissions(storagePermissions)
    }

    fun handleCameraAction(destination: Uri) {
        updateState { it.copy(pendingAction = PendingAction.CameraAction(destination)) }
        requestPermissions(storagePermissions)
    }
}

data class ProfileState(
        val avatar: String? = null,
        val name: String? = null,
        val about: String? = null,
        val rating: Int = 0,
        val respect: Int = 0,
        val pendingAction: PendingAction? = null
) : IViewModelState {
    override fun save(outState: SavedStateHandle) {
        outState.set("pendingAction", pendingAction)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return copy(pendingAction = savedState["pendingAction"])
    }
}

sealed class PendingAction : Parcelable {
    abstract val payload: Any?

    @Parcelize
    data class GalleryAction(override val payload: String) : PendingAction()

    @Parcelize
    data class SettingsAction(override val payload: Intent) : PendingAction()

    @Parcelize
    data class CameraAction(override val payload: Uri) : PendingAction()

    data class EditAction(override val payload: Pair<Uri, Uri>) : PendingAction(), Parcelable {

        constructor(parcel: Parcel) : this(Uri.parse(parcel.readString()) to Uri.parse(parcel.readString()))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(payload.first.toString())
            parcel.writeString(payload.second.toString())
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<EditAction> {
            override fun createFromParcel(parcel: Parcel): EditAction {
                return EditAction(parcel)
            }

            override fun newArray(size: Int): Array<EditAction?> {
                return arrayOfNulls(size)
            }
        }

    }
}
