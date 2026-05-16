package com.arduia.expense.screenshot

import android.app.Application
import com.arduia.expense.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class, qualifiers = "w360dp-h720dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BackupScreenshotTest {

    @Test
    fun backupLayout() {
        inflateLayout(R.layout.fragment_backup)
            .measureAndCapture("src/test/snapshots/backup/backup_layout.png")
    }

    @Test
    fun exportDialogLayout() {
        inflateLayout(R.layout.fragment_export_dialog)
            .measureAndCapture("src/test/snapshots/backup/export_dialog.png", heightDp = 300)
    }

    @Test
    fun backupItemLayout() {
        inflateLayout(R.layout.item_backup)
            .measureAndCapture("src/test/snapshots/backup/item_backup.png", heightDp = 80)
    }
}
