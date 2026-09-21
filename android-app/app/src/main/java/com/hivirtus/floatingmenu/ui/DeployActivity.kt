package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.core.AssetDeployer
import com.hivirtus.floatingmenu.databinding.ActivityDeployBinding
import com.hivirtus.floatingmenu.util.PermissionHelper
import com.hivirtus.floatingmenu.util.ShellUtils
import kotlin.concurrent.thread

class DeployActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeployBinding
    private val steps = mutableListOf<DeployStep>()
    private lateinit var adapter: DeployAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeployBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DeployAdapter(steps)
        binding.deployList.layoutManager = LinearLayoutManager(this)
        binding.deployList.adapter = adapter

        thread { runDeploy() }
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            markOverlayDone()
        }
    }

    private fun runDeploy() {
        val tasks = listOf(
            DeployTask(getString(R.string.check_root_access)) {
                if (!ShellUtils.isRootAvailable()) {
                    showRootFail()
                    Thread.sleep(Long.MAX_VALUE)
                }
            },
            DeployTask(getString(R.string.decompress_resources)) {
                AssetDeployer.deployAll(this)
            },
            DeployTask(getString(R.string.obtain_overlay_permission)) {
                while (!Settings.canDrawOverlays(this)) {
                    Handler(Looper.getMainLooper()).post {
                        PermissionHelper.ensureReady(this)
                    }
                    Thread.sleep(500)
                }
            }
        )

        binding.progressBar.max = tasks.size
        tasks.forEachIndexed { index, task ->
            addStep(task.title, running = true)
            task.run()
            completeStep(index)
            Handler(Looper.getMainLooper()).post {
                binding.progressBar.progress = index + 1
                binding.progressText.text = "(${index + 1}/${tasks.size})"
            }
        }

        Handler(Looper.getMainLooper()).post {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun showRootFail() {
        Handler(Looper.getMainLooper()).post {
            AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage(R.string.root_fail)
                .setCancelable(false)
                .setPositiveButton(R.string.okay) { _, _ -> finish() }
                .show()
        }
    }

    private fun addStep(title: String, running: Boolean) {
        Handler(Looper.getMainLooper()).post {
            steps.add(DeployStep(title, !running))
            adapter.notifyItemInserted(steps.size - 1)
        }
    }

    private fun completeStep(index: Int) {
        Handler(Looper.getMainLooper()).post {
            if (index < steps.size) {
                steps[index] = steps[index].copy(done = true)
                adapter.notifyItemChanged(index)
            }
        }
    }

    private fun markOverlayDone() {
        steps.indexOfFirst { it.title == getString(R.string.obtain_overlay_permission) }
            .takeIf { it >= 0 }?.let { completeStep(it) }
    }

    private data class DeployTask(val title: String, val run: () -> Unit)

    private data class DeployStep(val title: String, val done: Boolean)

    private class DeployAdapter(private val items: List<DeployStep>) :
        RecyclerView.Adapter<DeployAdapter.Holder>() {

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val msg: TextView = view.findViewById(R.id.deploy_item_msg)
            val ok: ImageView = view.findViewById(R.id.deploy_item_ok)
            val progress: ProgressBar = view.findViewById(R.id.deploy_item_progress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_deploy_step, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = items[position]
            holder.msg.text = item.title
            holder.ok.visibility = if (item.done) View.VISIBLE else View.GONE
            holder.progress.visibility = if (item.done) View.GONE else View.VISIBLE
        }

        override fun getItemCount(): Int = items.size
    }
}
