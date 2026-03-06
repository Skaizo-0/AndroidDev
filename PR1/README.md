
# Отчет по практической работе №1
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23  
**ФИО:** Волобуева Мария Александровна  
**Номер по списку:** 3


## 1. Цель работы
Целью данной практической работы является комплексное освоение инструментов проектирования и разработки мобильных приложений под ОС Android. В процессе выполнения задач требовалось:
* Изучить среду разработки **Android Studio**, настроить систему автоматизации сборки **Gradle** и устранить типичные ошибки конфигурации.
* Ознакомиться со структурой Android-проекта: назначением манифеста (`AndroidManifest.xml`), каталогов ресурсов (`res`) и файлов исходного кода.
* Освоить проектирование пользовательских интерфейсов с использованием различных контейнеров (`ViewGroup`): от классических `LinearLayout` и `TableLayout` до современного адаптивного `ConstraintLayout`.
* Изучить механизм управления ресурсами в зависимости от конфигурации устройства (поддержка портретной и альбомной ориентации).
* Реализовать программную логику на языке **Kotlin**, обеспечив взаимодействие между кодом и элементами разметки через обработчики событий (Listeners).



## 2. Структура проекта
Проект реализован по модульному принципу, что позволяет изолировать функционал различных заданий и изучать специфику настройки каждого модуля в отдельности:
1. **`layouttype`** — предназначен для изучения иерархии View и базовых способов позиционирования элементов с помощью весов и таблиц.
2. **`controllesson1`** — направлен на работу с адаптивной версткой в `ConstraintLayout` и создание квалифицированных ресурсов для горизонтальной ориентации (`layout-land`).
3. **`buttonclicker`** — модуль для отработки навыков программирования интерактивной логики, связи XML-разметки с Kotlin-кодом и использования различных подходов к обработке пользовательского ввода.



## 3. Выполнение работы

### 3.1 Настройка среды и решение проблем сборки
На начальном этапе была произведена настройка **Android Studio Hedgehog**. В процессе работы была выявлена и устранена ошибка `Your project path contains non-ASCII characters`, вызванная наличием кириллицы в пути к проекту. Для решения проблемы проект был перемещен в директорию с латинским путем, а в файл `gradle.properties` была добавлена директива `android.overridePathCheck=true`. Также была успешно проведена синхронизация **Gradle** для загрузки библиотек Kotlin 2.x.x.



### Задание 1. Модуль `layouttype` (ViewGroup)
**Цель:** Изучить способы распределения экранного пространства без жесткой привязки координат.

#### 1.1 Линейная разметка (LinearLayout)
В файле `linear_layout.xml` была реализована вложенная структура: корневой вертикальный контейнер содержит два горизонтальных ряда.
* **Техническая особенность:** Для обеспечения адаптивности использовано сочетание `android:layout_width="0dp"` и `android:layout_weight="1"`. Это позволяет системе динамически вычислять ширину кнопок на основе доступного пространства, игнорируя их фактический контент.

**Листинг `res/layout/linear_layout.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Первый ряд кнопок -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 1" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 2" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 3" />
    </LinearLayout>

    <!-- Второй ряд кнопок -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="12dp">
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 4" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 5" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button 6" />
    </LinearLayout>
</LinearLayout>
```

#### 1.2 Табличная разметка (TableLayout)
Для реализации `table_layout.xml` применен подход, основанный на строках `TableRow`.
* **Атрибуты:** Использован параметр `android:stretchColumns="*"`, который заставляет все колонки растягиваться до краев экрана. Продемонстрирована работа с объединением ячеек через `android:layout_span`, что необходимо для создания несимметричных интерфейсов.

**Листинг `table_layout.xml`:**
```xml

<?xml version="1.0" encoding="utf-8"?>
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:stretchColumns="*"
    android:padding="16dp">

    <TableRow>
        <Button android:text="BUTTON" />
        <TextView android:text="Table View" android:gravity="center" />
        <Button android:text="BUTTON" />
    </TableRow>

    <TableRow>
        <Button android:layout_span="2" android:text="SPAN BUTTON" />
        <CheckBox android:text="CheckBox" />
    </TableRow>

    <TableRow>
        <ImageButton android:src="@android:drawable/ic_lock_power_off" />
        <Button android:text="BUTTON" />
        <Button android:text="BUTTON" />
    </TableRow>
</TableLayout>
```
### Задание 2. Модуль `controllesson1` (ConstraintLayout и адаптивность)
**Цель:** Создать современный интерфейс и обеспечить его корректную работу при повороте устройства.

#### 2.1 Проектирование карточки контакта
В файле `activity_main.xml` реализован интерфейс профиля пользователя. Благодаря `ConstraintLayout` удалось создать "плоскую" структуру (без вложенных контейнеров), что оптимизирует производительность отрисовки. Элементы (`ImageView` для аватара, `TextView` для имени и `EditText` для ввода данных) связаны между собой "пружинами" (constraints), что гарантирует сохранение пропорций на любых экранах.

#### 2.2 Управление ресурсами и смена ориентации
Для решения проблемы перекрытия элементов в горизонтальном режиме (Landscape) была создана директория `res/layout-land`.
* В портретной версии 6 кнопок расположены вертикально.
* В альбомной версии (`layout-land/activity_second.xml`) разметка была перестроена в сетку 3х2, чтобы исключить выход элементов за границы видимости при повороте эмулятора.

**Листинг `MainActivity.kt` (controllesson1):**
```kotlin
package ru.mirea.mvolobueva.controllesson1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка макета, содержащего текстовое поле и кнопки
        setContentView(R.layout.activity_second)
    }
}
```

### Задание 3. Модуль `buttonclicker` (Программная логика)
**Цель:** Изучить взаимодействие Kotlin-кода с XML-интерфейсом и работу со слушателями событий.

В модуле реализована обработка нажатий двумя фундаментально разными способами:
1. **Программный (setOnClickListener):** Использован для кнопки `btnWhoAmI`. Позволяет назначать логику внутри кода с использованием лямбда-выражений, что является стандартом для современной разработки.
2. **Декларативный (android:onClick):** Использован для кнопки `btnItIsNotMe`. Метод прописывается в XML, а его реализация (`onMyButtonClick`) создается в Activity.

При взаимодействии с кнопками реализовано динамическое изменение состояния интерфейса: обновление текста в `TextView` и автоматическое переключение галочки в компоненте `CheckBox`.

**Листинг `MainActivity.kt` (buttonclicker):**
```kotlin
package ru.mirea.mvolobueva.buttonclicker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // Использование lateinit для отложенной инициализации View
    private lateinit var tvOut: TextView
    private lateinit var btnWhoAmI: Button
    private lateinit var checkBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация компонентов через поиск по ID
        tvOut = findViewById(R.id.tvOut)
        btnWhoAmI = findViewById(R.id.btnWhoAmI)
        checkBox = findViewById(R.id.checkBox)

        // Способ 1: Программная установка слушателя через лямбда-выражение
        btnWhoAmI.setOnClickListener {
            tvOut.text = "Мой номер по списку № 3"
            checkBox.isChecked = true
        }
    }

    // Способ 2: Обработка нажатия через метод, указанный в XML атрибуте onClick
    fun onMyButtonClick(view: View) {
        tvOut.text = "Это не я сделал"
        checkBox.isChecked = false
    }
}
```


## 4. Вывод
В ходе выполнения первой практической работы были получены и закреплены фундаментальные навыки разработки под платформу Android. Я научилась эффективно работать в среде Android Studio, настраивать зависимости через Gradle и проектировать пользовательские интерфейсы с использованием различных контейнеров.

Были освоены механизмы адаптации приложения под различные конфигурации экрана (Landscape), что является критически важным для создания качественного ПО. На практике изучен современный синтаксис языка **Kotlin**, методы работы с компонентами View, а также различные подходы к обработке пользовательских событий. Все поставленные задачи выполнены в полном объеме, приложение успешно протестировано на эмуляторе и корректно реагирует на действия пользователя.