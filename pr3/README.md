# Отчет по практической работе №3
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23 
**ФИО:** Волобуева Мария Александровна
**Номер по списку:** 3

---

## 1. Цель работы
Изучение механизмов взаимодействия между Activity и приложениями с помощью системы `Intent` (явные и неявные намерения). Освоение методов передачи данных между экранами, использование `Activity Result API` для получения результатов из дочерних активностей, а также работа с фрагментами (`Fragment`) и навигацией в сложных приложениях (Navigation Drawer).

---

## 2. Структура проекта
В рамках работы было создано два проекта: `Lesson3` и `MireaProject`.

### Проект `Lesson3` (Модули):
1. `IntentApp` — передача данных (системное время).
2. `Sharer` — обмен данными через системное меню (ACTION_SEND).
3. `FavoriteBook` — использование `Activity Result API`.
4. `SystemIntentsApp` — вызов системных приложений (звонки, браузер, карты).
5. `SimpleFragmentApp` — работа с фрагментами и адаптация под ориентацию экрана.

### Проект `MireaProject`:
* Контрольное задание: разработка приложения с боковым навигационным меню (Navigation Drawer) и внедрением WebView.

---

## 3. Выполнение работы

### 3.1 Модуль `IntentApp`
**Цель:** Передача данных через `Intent` и их обработка на втором экране.
В `MainActivity` реализовано получение системного времени в формате `yyyy-MM-dd HH:mm:ss`. Данные переданы во вторую Activity через `putExtra`.
![alt text](image.png)
**Листинг кода (передача данных):**
```kotlin
val dateString = sdf.format(Date(dateInMillis))
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("time_key", dateString)
startActivity(intent)
```

### 3.2 Модуль `Sharer`
**Цель:** Использование неявного намерения `ACTION_SEND`.
Приложение вызывает системный диалог выбора приложения для отправки текстовых данных.
![alt text](image-1.png)

**Листинг кода:**
```kotlin
val intent = Intent(Intent.ACTION_SEND)
intent.type = "*/*"
intent.putExtra(Intent.EXTRA_TEXT, "Mirea")
startActivity(Intent.createChooser(intent, "Выбор за вами!"))
```

### 3.3 Модуль `FavoriteBook`
**Цель:** Получение данных от дочерней активности через `ActivityResult API`.
Реализован контракт `StartActivityForResult`. Главная активность ожидает ввод названия книги от пользователя во второй активности и обновляет `TextView` после завершения работы второго экрана.
![alt text](image-2.png)
![alt text](image-3.png)
**Листинг обработки результата:**
```kotlin
private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val userBook = result.data?.getStringExtra(ShareActivity.USER_MESSAGE)
        textViewUserBook.text = "Название Вашей любимой книги: $userBook"
    }
}
```

### 3.4 Модуль `SystemIntentsApp`
**Цель:** Вызов системных приложений.
Реализованы три сценария:
1. `ACTION_DIAL` (телефонный набор).
2. `ACTION_VIEW` с URI `http` (браузер).
3. `ACTION_VIEW` с URI `geo` (карты).

### 3.5 Модуль `SimpleFragmentApp`

![alt text](image-6.png)
![alt text](image-5.png)
![alt text](image-7.png)
**Цель:** Динамическое управление фрагментами и адаптация под Landscape.
Созданы два фрагмента (`FirstFragment`, `SecondFragment`). 
* В вертикальной ориентации переключение реализовано программно через `FragmentManager` и `beginTransaction()`.
* В горизонтальной ориентации (`layout-land`) фрагменты размещены статично с помощью `FragmentContainerView` для отображения обоих фрагментов одновременно.

---

## 4. Контрольное задание: `MireaProject`
**Цель:** Создание приложения с навигационным меню (Navigation Drawer).

В проекте настроен `NavHostFragment` и навигационный граф `mobile_navigation.xml`. Добавлены два новых фрагмента: `DataFragment` (информация об отрасли IT) и `WebViewFragment` (клон браузера).

### Интеграция фрагментов:
1. **Навигационный граф:** Фрагменты добавлены как `destination` в `mobile_navigation.xml`.
2. **Меню:** Пункты меню добавлены в `activity_main_drawer.xml` с ID, соответствующими ID фрагментов.
3. **Логика MainActivity:** Метод `onOptionsItemSelected` дополнен обработкой нажатий на новые пункты меню, что позволяет переходить к фрагментам как из боковой шторки, так и из меню overflow (трех точек).

**Листинг настройки навигации:**
```kotlin
appBarConfiguration = AppBarConfiguration(
    setOf(R.id.nav_home, R.id.nav_webview, R.id.nav_data), drawerLayout
)
setupActionBarWithNavController(navController, appBarConfiguration)
```

---

## 5. Вывод
В ходе работы были изучены основные механизмы взаимодействия компонентов Android-приложения. Освоены навыки работы с `Intent` (как явными, так и неявными), что позволяет создавать связанные многоэкранные приложения. Успешно внедрена технология `Activity Result API` для обмена данными между экранами. Реализован механизм навигации с использованием фрагментов, включая создание адаптивных интерфейсов (Landscape/Portrait) и работу с боковым навигационным меню, что является стандартом при разработке современных мобильных приложений.