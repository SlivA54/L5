package com.example.l5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenSetup(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(viewModel: MainViewModel) {
    val allProducts by viewModel.allProducts.observeAsState(emptyList())
    val searchResults by viewModel.searchResults.observeAsState(emptyList())

    MainScreen(
        allProducts = allProducts,
        searchResults = searchResults,
        viewModel = viewModel
    )
}

@Composable
fun MainScreen(
    allProducts: List<Product>,
    searchResults: List<Product>,
    viewModel: MainViewModel
) {
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Поля ввода
        CustomTextField(
            title = "Название товара",
            textState = productName,
            onTextChange = { productName = it },
            keyboardType = KeyboardType.Text
        )
        CustomTextField(
            title = "Количество",
            textState = productQuantity,
            onTextChange = { productQuantity = it },
            keyboardType = KeyboardType.Number
        )

        // Кнопки - 3 в ряд + Очистить на новой строке
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Первая строка: 3 кнопки
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (productName.isNotEmpty() && productQuantity.isNotEmpty()) {
                            viewModel.insertProduct(
                                Product(
                                    productName = productName,
                                    quantity = productQuantity.toInt()
                                )
                            )
                            productName = ""
                            productQuantity = ""
                            searching = false
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text("+")
                }

                Button(
                    onClick = {
                        if (productName.isNotEmpty()) {
                            searching = true
                            viewModel.findProduct(productName)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text("Поиск")
                }

                Button(
                    onClick = {
                        if (productName.isNotEmpty()) {
                            searching = false
                            viewModel.deleteProduct(productName)
                            productName = ""
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text("Удалить")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Вторая строка: Очистить на весь экран
            Button(
                onClick = {
                    searching = false
                    productName = ""
                    productQuantity = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "Очистить все поля",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список продуктов
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val list = if (searching) searchResults else allProducts

            item {
                TitleRow(
                    head1 = "ID",
                    head2 = "Товар",
                    head3 = "Кол-во"
                )
            }

            items(list) { product ->
                ProductRow(
                    id = product.id,
                    name = product.productName,
                    quantity = product.quantity
                )
            }
        }
    }
}


@Composable
fun TitleRow(head1: String, head2: String, head3: String) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = head1,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = head2,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = head3,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.3f)
        )
    }
}

@Composable
fun ProductRow(id: Int, name: String, quantity: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = id.toString(),
                modifier = Modifier.weight(0.2f)
            )
            Text(
                text = name,
                modifier = Modifier.weight(0.5f)
            )
            Text(
                text = quantity.toString(),
                modifier = Modifier.weight(0.3f)
            )
        }
    }
}

@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = textState,
        onValueChange = onTextChange,
        label = { Text(title) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
