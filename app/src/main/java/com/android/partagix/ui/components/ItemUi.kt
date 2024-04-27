package com.android.partagix.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.user.User
import java.time.Duration
import java.util.Date

/**
 * Composable function to display an item, in a rectangle form.
 *
 * @param item an Item instance to display.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ItemUi(item: Item, user: User, loan: Loan) {
  val inventory = InventoryViewModel()
  val Date: Date =
      if (loan.startDate.before(Date())) {
        loan.endDate
      } else {
        loan.startDate
      }
  val time = Duration.between(Date().toInstant(), Date.toInstant())
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(PaddingValues(start = 10.dp, end = 10.dp))) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            modifier =
                Modifier.fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF939393),
                        shape = RoundedCornerShape(size = 4.dp))
                    .height(61.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 4.dp))
                    .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)) {
              Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.height(25.dp)) {
                  Text(text = user.rank, modifier = Modifier.width(50.dp))

                  Text(
                      text = user.name,
                      color = Color(0xff49454f),
                      lineHeight = 1.33.em,
                      style =
                          TextStyle(
                              fontSize = 18.sp,
                              // fontFamily = FontFamily(Font(R.font.inter)),
                              fontWeight = FontWeight(500),
                              color = Color(0xFF000000),
                              textAlign = TextAlign.Left,
                          ),
                      modifier = Modifier.fillMaxWidth())
                }
                Text(
                    text =
                        if (loan.idItem.equals("")) {
                          "not borrowed"
                        } else {
                          if (loan.startDate.before(Date())) {
                            "available in : /${Date}"
                          } else {
                            "borrowed in : /${Date}"
                          }
                        },
                    // color = Color(0xff49454f),
                    lineHeight = 1.43.em,
                    style = TextStyle(fontSize = 14.sp, letterSpacing = 0.25.sp),
                    modifier = Modifier.fillMaxWidth())
              }
              Column(modifier = Modifier.requiredHeight(height = 64.dp)) {
                Text(
                    text = item.name,
                    textAlign = TextAlign.End,
                    lineHeight = 1.45.em,
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            // fontFamily = FontFamily(Font(R.font.inter)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Right,
                        ),
                    modifier = Modifier.width(100.dp).height(40.dp).padding(top = 10.dp))
                Text(
                    text = "Quantity: " + item.quantity.toString(),
                    style =
                        TextStyle(
                            fontSize = 9.sp,
                            textAlign = TextAlign.Right,
                        ),
                    textAlign = TextAlign.End,
                    lineHeight = 0.8.em,
                    modifier = Modifier.width(100.dp).height(20.dp))
              }
              var imgBitmap: Bitmap? = null
              val imgFile = item.imageId
              if (imgFile.exists()) {
                // on below line we are creating an image bitmap variable
                // and adding a bitmap to it from image file.
                imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
              }

              Image(
                  painter = rememberAsyncImagePainter(model = imgBitmap),
                  contentDescription = "fds",
                  contentScale = ContentScale.FillBounds,
                  modifier = Modifier.width(70.dp))
            }
        Horizontalfullwidth()
      }
  // BuildingBlocksstatelayer1Enabled()

}

@Composable
fun Horizontalfullwidth(modifier: Modifier = Modifier) {
  Column(verticalArrangement = Arrangement.Center, modifier = modifier.fillMaxWidth()) {
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color(0xffcac4d0))
  }
}

@Composable
fun BuildingBlocksstatelayer1Enabled(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize().padding(bottom = 0.0000152587890625.dp))
}

/*@Preview
@Composable
fun ItemUiPreview() {
  ItemUi(
      ItemViewModel(Item)(
          "1",
          Category("1", "name"),
          "name",
          "description",
          Visibility.PUBLIC,
          1,
          android.location.Location("")))
}*/
