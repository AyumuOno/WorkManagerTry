package com.example.workmanagertry

import TestWorker
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagertry.ui.theme.WorkManagerTryTheme
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private val manager = WorkManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("Woker test").d("onCreate")
        setContent {
            WorkManagerTryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComposeLayout { startOneTimeWorkManager(it) }
                }
            }
        }
    }

    private fun startOneTimeWorkManager(editInput: String) {
        Timber.tag("Woker test").d("startWorkManager")

        // Dataクラスを用いてWorkerに情報を渡す
        val data = Data.Builder().apply {
            putString("message", editInput)
        }.build()

        /**
         * 特定条件を付与したworkerRequest作成
         */
        val constraints = Constraints.Builder().apply {
            // 充電中
            setRequiresCharging(true)
            // アイドル状態
//            setRequiresDeviceIdle(true)
        }.build()

        val workRequest = OneTimeWorkRequestBuilder<TestWorker>().apply {
            setConstraints(constraints)
            setInputData(data)
        }.build()

        /**
         * 特に指定なしのworkerRequest作成
         */
        val request = OneTimeWorkRequest.from(TestWorker::class.java)

        manager.enqueueUniqueWork("unique", ExistingWorkPolicy.KEEP, workRequest)
    }

    /**
     * 反復のWorkerを設定
     * workerIdからキューにいれたworker情報を取得、キャンセルすることも可能
     */
    private fun startPeriodicWorkManager() {
        val workerTag = "periodic"
        Timber.tag("Woker test").d("startPeriodicWorkManager")

        val workRequest = OneTimeWorkRequestBuilder<TestWorker>().apply {
            setInitialDelay(10, TimeUnit.SECONDS)
            setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        }.apply {
            addTag(workerTag)
        }.build()

        manager.enqueue(workRequest)

        var workerId: UUID? = null
        workerId = workRequest.id

        // 型はListenableFuture<WorkInfo>
        val info = manager.getWorkInfoById(workerId)
        // 型はListenableFuture<MutableList<WorkInfo>>
        val infoByTags = manager.getWorkInfosByTag(workerTag)

        Toast.makeText(this, "State:${info.get().state}", Toast.LENGTH_LONG).show()
        infoByTags.get()
            .forEach { Toast.makeText(this, "State:${it.state}", Toast.LENGTH_LONG).show() }

        // キャンセル処理（cancelAllWorkはアプリの動作に影響を与える可能性があり非推奨）
        manager.cancelAllWork()
        manager.cancelWorkById(workerId)
        manager.cancelAllWorkByTag(workerTag)


    }
}


@Composable
fun ComposeLayout(
    startWorkManager: (String) -> Unit
) {
    val editInput = remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = editInput.value,
            onValueChange = { editInput.value = it }
        )

        Button(
            onClick = {
                startWorkManager(editInput.value)
            }
        ) {
            Text(text = "WorkManagerスタート")
        }
    }
}

@Preview
@Composable
fun PreviewComposeLayout() {
    ComposeLayout { }
}
