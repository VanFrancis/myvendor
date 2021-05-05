package com.mobilepoc.myvendor.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.data.entites.Product
import com.mobilepoc.myvendor.view.activities.CartListActivity
import com.mobilepoc.myvendor.view.activities.SettingsActivity
import com.mobilepoc.myvendor.view.adapters.DashboardItemsListAdapter
import com.mobilepoc.myvendor.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                startActivity(Intent(requireContext(),SettingsActivity::class.java))
                return true
            }
            R.id.action_cart ->{
                startActivity(Intent(requireContext(),CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }


    /**
     * Função para obter a lista de itens do painel do Firestore
     */
    private fun getDashboardItemsList() {
        showProgressDialog()

        FireStoreClass().getDashboardItemsList(this@DashboardFragment)
    }
    /**
     * Função para obter o resultado de sucesso dos itens do painel do Cloud Firestore.
     */
    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {

        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapter


        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }
}