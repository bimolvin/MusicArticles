package com.example.musicarticles.editor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.musicarticles.R
import com.example.musicarticles.articleDetail.ArticleDetailActivity
import com.example.musicarticles.articleDetail.ArticleDetailViewModel
import com.example.musicarticles.articleList.ARTICLE_ID
import com.example.musicarticles.articleList.ArticleListViewModel
import com.example.musicarticles.data.Article
import com.example.musicarticles.data.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

const val ARTICLE_TO_EDIT_ID = "id"

class EditorFragment : Fragment(), EditorSubmitFragment.OnFragmentSendDataListener {
    private val articleListViewModel: ArticleListViewModel by viewModels()
    private val articleViewModel: ArticleDetailViewModel by viewModels()
    private lateinit var articleUri: Uri

    private var id: Long? = null
    private var article: Article? = null
    private lateinit var titleTextField: TextInputEditText
    private lateinit var contentTextField: TextInputEditText
    private lateinit var editorFragment: EditorSubmitFragment
    private val listener: EditorSubmitFragment.OnFragmentSendDataListener = this

    enum class MessageTag {
        ADD_SUCCESS, ADD_ERROR, UPDATE_ERROR, TITLE_EMPTY, CONTENT_EMPTY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARTICLE_TO_EDIT_ID)?.toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextField = view.findViewById(R.id.title_text_field)
        contentTextField = view.findViewById(R.id.content_text_field)
        val navButton = view.findViewById<Button>(R.id.next)

        id?.let {
            navButton.text = resources.getString(R.string.editor_save)
            articleUri = Uri.parse("content://com.example.art-music/articles/$id")
            article = articleViewModel.fetchArticle(articleUri)
            titleTextField.setText(article?.title)
            contentTextField.setText(article?.content)
        }

        navButton.setOnClickListener { _ ->
            if(noEmptyFields()) {
                if(article == null) {
                    moveToEditorSubmit()
                } else {
                    editArticle()
                }
            }
        }
    }

    private fun noEmptyFields() : Boolean {
        return if(!titleTextField.text.isNullOrEmpty() && !contentTextField.text.isNullOrEmpty()) {
            true
        } else {
            if(titleTextField.text.isNullOrEmpty()) {
                message(tag = MessageTag.TITLE_EMPTY)
            } else if(contentTextField.text.isNullOrEmpty()) {
                message(tag = MessageTag.CONTENT_EMPTY)
            }
            false
        }
    }

    private fun moveToEditorSubmit() {
        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        editorFragment = EditorSubmitFragment(listener)
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.to_left_in, R.anim.to_left_out, R.anim.to_right_in, R.anim.to_right_out)
            .replace(R.id.container, editorFragment)
            .commit()
    }

    private fun moveBack() {
        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.to_right_in, R.anim.to_right_out, R.anim.to_left_in, R.anim.to_left_out)
            .replace(R.id.container, this)
            .commit()
    }

    override fun onButtonBack() {
        moveBack()
    }

    override fun onDataReceived(id: Long) {
        moveBack()

        if(addArticle(id) == Activity.RESULT_OK) {
            titleTextField.text?.clear()
            contentTextField.text?.clear()
            message(tag = MessageTag.ADD_SUCCESS)
        } else {
            message(tag = MessageTag.ADD_ERROR)
        }
    }

    private fun addArticle(id: Long) : Int {
        return if (noEmptyFields()) {
            val added = articleListViewModel.addNewArticle(
                id = id,
                title = titleTextField.text.toString(),
                content = contentTextField.text.toString(),
                author = resources.getString(R.string.user_name)
            )
            User.userLastUpdate = Calendar.getInstance().time

            if(added) Activity.RESULT_OK else Activity.RESULT_CANCELED
        } else {
            Activity.RESULT_CANCELED
        }
    }

    private fun editArticle() {
        val articleTitle = titleTextField.text.toString()
        val articleContent = contentTextField.text.toString()

        article?.title = articleTitle
        article?.content = articleContent
        val updated = articleViewModel.updateArticle(article!!)

        if(updated) {
            val intent = Intent(activity, ArticleDetailActivity()::class.java)
            intent.putExtra(ARTICLE_ID, article!!.id)
            startActivity(intent)
        } else {
            message(tag = MessageTag.UPDATE_ERROR)
        }
    }

    private fun message(tag: MessageTag) {
        view?.let {
            when(tag) {
                MessageTag.ADD_SUCCESS ->
                    Snackbar.make(it, R.string.message_add_success, Snackbar.LENGTH_SHORT).show()

                MessageTag.ADD_ERROR ->
                    Snackbar.make(it, R.string.message_add_error, Snackbar.LENGTH_SHORT).show()

                MessageTag.UPDATE_ERROR ->
                    Snackbar.make(it, R.string.message_update_error, Snackbar.LENGTH_SHORT).show()

                MessageTag.TITLE_EMPTY ->
                    Snackbar.make(it, R.string.message_fill_title, Snackbar.LENGTH_SHORT).show()

                MessageTag.CONTENT_EMPTY ->
                    Snackbar.make(it, R.string.message_fill_content, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String? = null) =
            EditorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARTICLE_TO_EDIT_ID, id)
                }
            }
    }
}