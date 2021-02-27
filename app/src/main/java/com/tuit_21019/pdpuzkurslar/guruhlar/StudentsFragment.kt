package com.tuit_21019.pdpuzkurslar.guruhlar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tuit_21019.pdpuzkurslar.DataBase.DbHelper
import com.tuit_21019.pdpuzkurslar.R
import com.tuit_21019.pdpuzkurslar.guruhlar.adapters.StudentsAdapter
import com.tuit_21019.pdpuzkurslar.models.Guruh
import com.tuit_21019.pdpuzkurslar.models.Talaba
import kotlinx.android.synthetic.main.fragment_group_item.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "group"
private const val ARG_PARAM2 = "param2"

class GroupItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var group: Guruh? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            group = it.getSerializable(ARG_PARAM1) as Guruh?
            param2 = it.getString(ARG_PARAM2)
        }
        adapter = StudentsAdapter()
    }

    lateinit var root: View
    private var db: DbHelper? = null
    private var studentList: ArrayList<Talaba>? = null
    private var adapter: StudentsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_group_item, container, false)
        db = DbHelper(this.requireContext())
        Log.d("AAAA", "groupID: ${group?.id}")
        setToolbar()
        loadDataToView()
        loadData()
        loadAdapters()
        deleteItemClick()
        addClick()

        return root
    }

    private fun addClick() {
        root.toolbar.setOnMenuItemClickListener {

            val bundle = Bundle()
            bundle.putSerializable("group_to_student_add", group)
            findNavController().navigate(R.id.addStudent,bundle)
            true
        }
    }

    private fun deleteItemClick() {
        adapter?.setOnDeleteClick(object : StudentsAdapter.OnDeleteClick {
            override fun onClick(talaba: Talaba, position: Int) {
                val dialog = AlertDialog.Builder(root.context)
                dialog.setTitle("Talabani o'chirish")
                dialog.setMessage("Ushbu o'quvchini o'chirmoqchimisiz?")
                dialog.setPositiveButton("Ha") { p0, p1 ->
                    db?.deleteTalaba(talaba.id!!)
                    adapter?.notifyItemRemoved(position)
                    adapter?.setAdapter(db?.getAllStudentsByGroupId(group?.id!!)!!)
                    p0.cancel()
                    Toast.makeText(root.context, "O'chirildi", Toast.LENGTH_SHORT).show()
                }
                dialog.setNegativeButton("Yo'q") { p0, p1 ->
                    p0?.cancel() }
                dialog.show()
            }

        })
    }

    private fun setToolbar() {
        root.toolbar.title = group?.guruh_nomi

        if (group?.ochilganligi == 1) {
            root.toolbar.menu.getItem(0).isVisible = false
        } else if (group?.ochilganligi == 0) {
            root.toolbar.menu.getItem(0).isVisible = true
        }

        root.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


    }

    private fun loadAdapters() {
        adapter?.setAdapter(studentList!!)
        root.item_group_rv.adapter = adapter
    }

    private fun loadData() {
        studentList = ArrayList()
        studentList = db?.getAllStudentsByGroupId(group?.id!!)
        if (db?.getAllStudentsByGroupId(group?.id!!)!!.isEmpty()) {

            studentList?.add(
                Talaba(
                    "Olimjon",
                    "Rustamov",
                    "Odil o'g'li",
                    "12/12/2021",
                    1,
                    "Juft",
                    group?.dars_vaqti,
                    group?.id
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataToView() {
        root.item_group_name.text = "PDP ${group?.guruh_nomi}"
        val studentCount = db?.getAllStudentsByGroupId(group?.id!!)?.size
        root.item_group_student_count.text = "O'quvchilar soni: $studentCount"
        root.item_group_time.text = "Vaqti: ${group?.dars_vaqti}"

        if (group?.ochilganligi == 1) {
            root.item_group_start_lesson.visibility = View.GONE
        } else {
            root.item_group_start_lesson.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(group: String, param2: String) =
            GroupItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, group)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}