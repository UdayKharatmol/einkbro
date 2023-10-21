package info.plateaukao.einkbro.view.dialog.compose

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import info.plateaukao.einkbro.view.compose.MyTheme
import info.plateaukao.einkbro.view.data.MenuInfo
import info.plateaukao.einkbro.viewmodel.ActionModeMenuViewModel

class ActionModeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {
    private lateinit var actionModeMenuViewModel: ActionModeMenuViewModel
    private lateinit var menuInfos: List<MenuInfo>

    @Composable
    override fun Content() {
        val text by actionModeMenuViewModel.selectedText.collectAsState()
        MyTheme {
            ActionModeMenu(menuInfos) { intent ->
                if (intent != null) {
                    context.startActivity(intent.apply {
                        putExtra(Intent.EXTRA_PROCESS_TEXT, text)
                        putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
                    })
                }

                actionModeMenuViewModel.updateActionMode(null)
            }
        }
    }

    fun init(
        actionModeMenuViewModel: ActionModeMenuViewModel,
        menuInfos: List<MenuInfo>,
    ) {
        this.actionModeMenuViewModel = actionModeMenuViewModel
        this.menuInfos = menuInfos
    }
}

@Composable
private fun ActionModeMenu(
    menuInfos: List<MenuInfo>,
    onClicked: (Intent?) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .wrapContentHeight()
            .width(280.dp)
            .border(1.dp, MaterialTheme.colors.onBackground, RoundedCornerShape(7.dp))
    ) {
        items(menuInfos.size) { index ->
            val info = menuInfos[index]
            ActionMenuItem(info.title, info.icon) {
                info.action?.invoke()
                if (info.intent != null || info.closeMenu) onClicked(info.intent)
            }
        }
    }
}

@Composable
fun ActionMenuItem(
    title: String,
    iconDrawable: Drawable?,
    onClicked: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val borderWidth = if (pressed) 0.5.dp else (-1).dp

    val configuration = LocalConfiguration.current
    val width = when {
        configuration.screenWidthDp > 500 -> 55.dp
        else -> 45.dp
    }

    val fontSize = if (configuration.screenWidthDp > 500) 10.sp else 8.sp
    Column(
        modifier = Modifier
            .width(width)
            .wrapContentHeight()
            .padding(8.dp)
            .border(borderWidth, MaterialTheme.colors.onBackground, RoundedCornerShape(7.dp))
            .clickable(
                indication = null,
                interactionSource = interactionSource,
            ) { onClicked() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberDrawablePainter(drawable = iconDrawable),
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .padding(horizontal = 6.dp),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            text = title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = fontSize,
            fontSize = fontSize,
            color = MaterialTheme.colors.onBackground
        )
    }
}
