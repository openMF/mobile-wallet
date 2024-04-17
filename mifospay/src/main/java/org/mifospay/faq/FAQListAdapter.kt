package org.mifospay.faq

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import org.mifospay.R

/**
 * This class is the Adapter class for FAQ Section.
 *
 * @author ankur
 * @since 11/July/2018
 */
class FAQListAdapter(
    private val context: Context, // group titles
    private var listDataGroup: List<String>,
    // child data
    private var listDataChild: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {
    init {
        listDataChild = listDataChild
    }

    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return listDataChild[listDataGroup[groupPosition]]
            ?.get(childPosititon)!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View? {
        var convertView = convertView
        val childText = getChild(groupPosition, childPosition) as String
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.faq_list_child, parent,false)
        }
        val textViewChild = convertView
            ?.findViewById<TextView>(R.id.faq_list_child)
        textViewChild?.text = childText
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataChild[listDataGroup[groupPosition]]
            ?.size!!
    }

    override fun getGroup(groupPosition: Int): Any {
        return listDataGroup[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listDataGroup.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View? {
        var convertView = convertView
        val headerTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.faq_list_group, parent,false)
        }
        val textViewGroup = convertView
            ?.findViewById<TextView>(R.id.faq_list_group)
        textViewGroup?.setTypeface(null, Typeface.BOLD)
        textViewGroup?.text = headerTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}