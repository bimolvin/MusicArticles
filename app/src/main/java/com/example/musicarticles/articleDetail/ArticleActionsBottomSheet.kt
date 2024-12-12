package com.example.musicarticles.articleDetail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.musicarticles.R
import com.example.musicarticles.data.Article
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog


class ArticleActionsBottomSheet(listener: DialogListener, private val article: Article)
    : BottomSheetDialogFragment() {

    private var mBottomSheetListener: DialogListener? = null

    init {
        this.mBottomSheetListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.article_bottom_sheet, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = STATE_EXPANDED
        return dialog.apply {
            setOnShowListener {
                view?.findViewById<Button>(R.id.button_copy_link)?.setOnClickListener {
                    mBottomSheetListener?.copyArticleLink(article)
                }
                view?.findViewById<Button>(R.id.button_edit)?.setOnClickListener {
                    mBottomSheetListener?.editArticle(article)
                }
                view?.findViewById<Button>(R.id.button_delete)?.setOnClickListener {
                    mBottomSheetListener?.deleteArticle(article)
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** attach listener from parent fragment */
        try {
            mBottomSheetListener = context as DialogListener?
        } catch (_: ClassCastException) {
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    interface DialogListener {
        fun copyArticleLink(article: Article)
        fun editArticle(article: Article)
        fun deleteArticle(article: Article)
    }
}