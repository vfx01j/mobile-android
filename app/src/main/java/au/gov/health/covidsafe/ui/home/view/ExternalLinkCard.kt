package au.gov.health.covidsafe.ui.home.view

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import au.gov.health.covidsafe.R
import au.gov.health.covidsafe.networking.response.Message
import kotlinx.android.synthetic.main.view_card_external_link_card.view.*


class ExternalLinkCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_card_external_link_card, this, true)

        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ExternalLinkCard)
            val icon = a.getDrawable(R.styleable.ExternalLinkCard_external_linkCard_icon)
            val iconVisible = a.getBoolean(R.styleable.ExternalLinkCard_external_linkCard_icon_visible, true)
            val title = a.getString(R.styleable.ExternalLinkCard_external_linkCard_title)
            val content = a.getString(R.styleable.ExternalLinkCard_external_linkCard_content)
            val padding = a.getDimension(R.styleable.ExternalLinkCard_external_linkCard_icon_padding, 0f).toInt()
            val iconBackground = a.getResourceId(R.styleable.ExternalLinkCard_external_linkCard_icon_background, R.color.transparent)
            val textColorResId = a.getResourceId(R.styleable.ExternalLinkCard_external_linkCard_text_color, R.color.slack_black)
            val textColor = ContextCompat.getColor(context, textColorResId)

            external_link_round_image.setImageDrawable(icon)
            external_link_round_image.visibility = if (iconVisible) View.VISIBLE else View.GONE
            external_link_round_image.setBackgroundResource(iconBackground)
            external_link_round_image.setPadding(padding, padding, padding, padding)
            external_link_headline.text = title
            external_link_content.text = content

            setTextColor(textColor)

            a.recycle()
        }
    }

    private fun setTextColor(textColor: Int) {
        external_link_headline.setTextColor(textColor)
        external_link_content.setTextColor(textColor)

        val icChevron =
                if (textColor == ContextCompat.getColor(context, R.color.error)) {
                    R.drawable.ic_chevron_right_red
                } else {
                    R.drawable.ic_chevron_right
                }

        next_arrow.setImageDrawable(
                ContextCompat.getDrawable(context, icChevron).also {
                    it?.isAutoMirrored = true
                }
        )
    }

    fun setMessage(message: Message) {
        external_link_round_image.visibility = View.GONE

        external_link_headline.text = message.title
        external_link_content.text = message.body

        this.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(message.destination)).also {
                context.startActivity(it)
            }
        }
    }

    fun setTitleBodyAndClickCallback(title: String, body: String, clickCallBack: () -> Unit) {
        external_link_headline.text = title
        external_link_content.text = body

        this.setOnClickListener {
            clickCallBack()
        }
    }

    fun setErrorTextColor() {
        setTextColor(ContextCompat.getColor(context, R.color.error))
    }

    fun setTopRightIcon(iconResID: Int) {
        next_arrow.setImageResource(iconResID)
    }

    fun setColorForContentWithAction() {
        val actionColor = ContextCompat.getColor(context, R.color.dark_green)
        val actionColorString = String.format("%X", actionColor).substring(2)

        val stringBuilder = StringBuilder()

        external_link_content.text.split("\n").forEachIndexed { index, s ->
            if (index == 0) {
                stringBuilder.append(s)
            } else {
                stringBuilder.append("<br/><font color='#$actionColorString'>$s</font>")
            }
        }

        val htmlText = stringBuilder.toString()

        external_link_content.text =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(htmlText)
                }
    }
}