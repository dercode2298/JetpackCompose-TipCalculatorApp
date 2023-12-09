package com.example.tipcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculatorapp.components.InputField
import com.example.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.example.tipcalculatorapp.widgets.RoundedIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                val totalPerPersonState = remember {
                    mutableStateOf(0f)
                }
                Column( modifier = Modifier.padding(20.dp)) {
                    TopHeader(totalPerPersonState.value.toDouble())
                    Spacer(modifier = Modifier.height(10.dp))
                    MainContent(){
                        totalPerPersonState.value = it
                    }
                }
            }
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = RoundedCornerShape(10.dp)),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = MaterialTheme.typography.titleSmall)
            Text(text = "$${total}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun MainContent(
    onValueChange: (Float) -> Unit
){
    BillForm(){ billAmount ->
        onValueChange(billAmount)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier, onValueChange: (Float) -> Unit = {}){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val tipAmountState = remember {
        mutableStateOf(0)
    }
    val splitAmountState = remember {
        mutableStateOf(1)
    }
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enable = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) return@KeyboardActions

                    onValueChange((totalBillState.value.toFloat() + tipAmountState.value)/splitAmountState.value)

                    keyboardController?.hide()
                }
            )

            if(validState){
                SplitBill{
                    splitAmountState.value =  it
                    onValueChange((totalBillState.value.toFloat() + tipAmountState.value)/splitAmountState.value)
                }
                TipValue(tipAmountState.value)
                PercentageSplit{
                    if( totalBillState.value.isNotEmpty() && totalBillState.value.toInt() > 1){
                        tipAmountState.value = (it.toInt() * totalBillState.value.toInt()) / 100
                        onValueChange((totalBillState.value.toFloat() + tipAmountState.value)/splitAmountState.value)
                    }
                }
            } else{
                Box{}
            }

        }
    }
}

@Composable
fun PercentageSplit(
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {
    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
       // horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = Modifier.weight(1f),
            value = sliderPositionState.floatValue,
            onValueChange = {newVal ->
               // Log.d("check value",newVal.toString())
                sliderPositionState.floatValue = newVal
            },
            steps = 5,
            onValueChangeFinished = {
                onValueChange(sliderPositionState.floatValue)
            },
            valueRange = 0f..100f
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${sliderPositionState.floatValue.toInt()}%",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun TipValue(tipAmountState: Int = 0) {
    Row(
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Text(
            text = "Tip",
            modifier = Modifier
                .padding(start = 10.dp)
                .align(alignment = Alignment.CenterVertically),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "${tipAmountState}$",
            modifier = Modifier
                .padding(end = 44.dp)
                .align(alignment = Alignment.CenterVertically),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun SplitBill(
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
){
    val splitValueState = remember {
        mutableIntStateOf(1)
    }
    Row (
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,

        ) {
        Text(
            text = "Split",
            modifier = Modifier
                .padding(start = 10.dp)
                .align(alignment = Alignment.CenterVertically)
                .weight(1f),
            style = MaterialTheme.typography.titleSmall
        )
        //Spacer(modifier = Modifier.width(100.dp))
        Row(modifier = Modifier.weight(1f)) {
            RoundedIconButton(
                imageVector = Icons.Default.Remove,
                onClick = {
                    if (splitValueState.intValue > 1){
                        splitValueState.intValue--
                        onValueChange(splitValueState.intValue)
                    }
                }
            )
            Text(
                text = "${splitValueState.intValue}",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )

            RoundedIconButton(
                imageVector = Icons.Default.Add,
                onClick = {
                    splitValueState.intValue++
                    onValueChange(splitValueState.intValue)
                }
            )
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    TipCalculatorAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApp {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            TopHeader()
            //MainContent()
        }
    }
}