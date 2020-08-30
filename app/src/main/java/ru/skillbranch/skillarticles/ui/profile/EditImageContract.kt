package ru.skillbranch.skillarticles.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

/**
 * @author mmikhailov on 30.08.2020.
 */
class EditImageContract : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {
    override fun createIntent(context: Context, input: Pair<Uri, Uri>?): Intent {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            setDataAndType(input!!.first, "image/jpeg")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(MediaStore.EXTRA_OUTPUT, input.second)
            putExtra("return-value", true)
        }

        // grant write URI permission for suitable activities
        context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { info -> info.activityInfo.packageName }
            .forEach { resolvedPackage ->
                context.grantUriPermission(
                        resolvedPackage,
                        input!!.second,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK) intent?.data
        else null
    }
}