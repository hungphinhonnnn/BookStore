package fpolcom.example.foly.ph37745.bookstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Indigo = Color(0xFF3F51F5)
private val LightBackground = Color(0xFFF5F6FA)
private val TextDark = Color(0xFF1F2933)
private val TextGray = Color(0xFF9AA0A6)
private val Orange = Color(0xFFFF7A00)
private val Purple = Color(0xFF7C4DFF)
private val DividerColor = Color(0xFFEDEDED)

private val BookStoreColors = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = Color(0xFF1A237E),
    secondary = Purple,
    surface = Color.White,
    background = LightBackground,
    onSurface = TextDark,
    outlineVariant = DividerColor
)

@Composable
fun BookStoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = BookStoreColors, content = content)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookStoreTheme {
                ProfileScreen()
            }
        }
    }
}

data class StatItem(
    val value: String,
    val label: String,
    val underlined: Boolean = false
)

data class MenuActionItem(
    val title: String,
    val icon: ImageVector
)

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Cá nhân",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Indigo,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun AvatarWithEdit() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFFE8EAF6))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(CircleShape)
                .background(Indigo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(54.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Indigo)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Chỉnh sửa",
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun UserHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarWithEdit()
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Elena Rodriguez",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "elena.rodriguez@example.com",
            fontSize = 14.sp,
            color = TextGray
        )
    }
}

@Composable
fun StatsCard() {
    val stats = listOf(
        StatItem(value = "24", label = "Sách đã mua"),
        StatItem(value = "12", label = "Yêu thích", underlined = true),
        StatItem(value = "4.8", label = "Đánh giá")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            stats.forEachIndexed { index, stat ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stat.value,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stat.label,
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        if (stat.underlined) {
                            Spacer(Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(2.dp)
                                    .background(Purple, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }

                if (index < stats.lastIndex) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(DividerColor)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = Indigo
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD)
        )
    }
}

@Composable
fun MenuCard() {
    val items = listOf(
        MenuActionItem(title = "Thông tin cá nhân", icon = Icons.Default.Person),
        MenuActionItem(title = "Lịch sử đơn hàng", icon = Icons.Default.History),
        MenuActionItem(title = "Sách yêu thích", icon = Icons.Default.FavoriteBorder),
        MenuActionItem(title = "Đổi mật khẩu", icon = Icons.Default.Lock),
        MenuActionItem(title = "Cài đặt", icon = Icons.Default.Settings)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            items.forEachIndexed { index, item ->
                MenuItem(title = item.title, icon = item.icon, onClick = {})
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 54.dp),
                        thickness = 1.dp,
                        color = DividerColor
                    )
                }
            }
        }
    }
}

@Composable
fun LogoutButton() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Orange)
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Đăng xuất",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun SupportSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "HỖ TRỢ & PHÁP LÝ",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextGray,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Chính sách bảo mật",
            fontSize = 14.sp,
            color = TextGray
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Điều khoản dịch vụ",
            fontSize = 14.sp,
            color = TextGray
        )
    }
}

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem(title = "Trang chủ", icon = Icons.Default.Home),
        BottomNavItem(title = "Danh mục", icon = Icons.Default.Category),
        BottomNavItem(title = "Giỏ hàng", icon = Icons.Default.ShoppingCart),
        BottomNavItem(title = "Cá nhân", icon = Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo,
                    selectedTextColor = Indigo,
                    indicatorColor = Indigo.copy(alpha = 0.12f),
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    var selectedTab by remember { mutableStateOf(3) }

    Scaffold(
        containerColor = LightBackground,
        topBar = { ProfileTopBar() },
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { UserHeader() }
            item { StatsCard() }
            item { MenuCard() }
            item { LogoutButton() }
            item { SupportSection() }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    BookStoreTheme {
        ProfileScreen()
    }
}
