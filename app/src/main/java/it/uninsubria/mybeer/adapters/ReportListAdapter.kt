package it.uninsubria.mybeer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.listeners.ReportClickListener

class ReportListAdapter(
    private var reportList: ArrayList<Report?>,
    private val listener: ReportClickListener
): RecyclerView.Adapter<ReportViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_list, parent, false)
        return ReportViewHolder(listener, view)
    }
    override fun getItemCount(): Int = reportList.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int){
        holder.tvReport.text = reportList[position]?.beer_name
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(reportList: ArrayList<Report?>) {
        this.reportList = reportList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Report?> { return reportList }

}

class ReportViewHolder(listener: ReportClickListener, itemView: View): RecyclerView.ViewHolder(itemView){
    private val reportContainer: CardView = itemView.findViewById(R.id.report_container)
    val tvReport: TextView = itemView.findViewById(R.id.tvReport)

    init{
        itemView.setOnLongClickListener{
            listener.onLongClick(adapterPosition, reportContainer)
            true
        }
    }

}
