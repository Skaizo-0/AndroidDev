# Отчет по практической работе №1
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23  
**ФИО:** Волобуева Мария Александровна  
**Номер по списку:** 3  

---

## 1. Цель работы
Освоить базовые принципы разработки мобильных приложений в среде Android Studio. Изучить структуру проекта, основные виды макетов (Layouts), работу с ресурсами, жизненный цикл Activity и методы обработки событий (нажатий) с использованием современного языка разработки Kotlin.

## 2. Структура проекта
В рамках выполнения практической работы был создан один общий проект, внутри которого для каждого логического блока заданий были реализованы независимые модули:
1. `layouttype` — изучение базовых контейнеров (LinearLayout, TableLayout).
2. `control_lesson1` — работа с современным ConstraintLayout и создание альтернативных разметок (Landscape).
3. `ButtonClicker` — программирование логики и обработчиков событий (Listeners).

---

## 3. Выполнение работы

### Задание 1. Модуль `layouttype` (Изучение макетов ViewGroup)
**Цель:** Создать экраны на основе устаревших, но структурно важных макетов `LinearLayout` и `TableLayout`.

#### 1.1 LinearLayout
Был создан файл `linear_layout.xml`. Корневым элементом задан вертикальный `LinearLayout`, внутри которого размещены два горизонтальных `LinearLayout`. Для равномерного распределения кнопок по ширине экрана использованы веса (`layout_weight="1"`).

**Листинг `res/layout/linear_layout.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
        <Button android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:text="Button" />
    </LinearLayout>
</LinearLayout>
```

#### 1.2 TableLayout
Был создан файл table_layout.xml. Для равномерного распределения колонок использован атрибут корневого тега android:stretchColumns="*". Для объединения ячеек во второй строке применен атрибут android:layout_span="2".

**Листинг res/layout/table_layout.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:stretchColumns="*"
    android:padding="16dp">

    <TableRow>
        <Button android:text="BUTTON" />
        <TextView android:text="This is Table View!" android:gravity="center" />
        <Button android:text="BUTTON" />
    </TableRow>

    <TableRow>
        <Button android:layout_span="2" android:text="BUTTON" />
        <CheckBox android:text="CheckBox" />
    </TableRow>

    <TableRow>
        <ImageButton android:src="@android:drawable/ic_lock_power_off" android:contentDescription="Power" />
        <Button android:text="BUTTON" />
        <Button android:text="BUTTON" />
    </TableRow>
</TableLayout>
```

### Задание 2. Модуль control_lesson1 (ConstraintLayout и ориентация)

![alt text](image.png)

![alt text](image-1.png)

**Цель:** Создать пользовательский интерфейс с использованием ConstraintLayout и реализовать смену ориентации экрана.

#### 2.1 Верстка экрана контакта

В файле activity_main.xml с использованием ConstraintLayout сверстан интерфейс "Карточка контакта". Использованы элементы: ImageView (аватар), поля ввода EditText (с использованием атрибута hint для accessibility и подсказок) и Button. Все элементы привязаны друг к другу с помощью constraints (пружин), что обеспечивает адаптивность на экранах разных размеров.

#### 2.2 Смена ориентации экрана (Landscape)

Для изучения поведения приложения при повороте устройства был создан файл activity_second.xml, содержащий текст и 6 кнопок. Чтобы кнопки не уходили за границы экрана в горизонтальном положении, была создана альтернативная разметка в папке layout-land. В MainActivity.kt изменен стартовый экран.

**Листинг MainActivity.kt:**

```Kotlin
package ru.mirea.lesson1.controllesson1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}
```

### Задание 3. Модуль ButtonClicker (Обработчики событий)

![](2026-05-24-19-01-12.png)

**Цель:** Изучить способы обработки событий (нажатий) на элементы интерфейса с использованием Kotlin.

В разметке activity_main.xml созданы: TextView, две кнопки (Button) и CheckBox. Обработка нажатий реализована двумя разными способами:
	1. Первая кнопка (Who Am I?): Обрабатывается через программную установку слушателя setOnClickListener (используя лямбда-выражения Kotlin).
	2. Вторая кнопка (It's Not Me): Обрабатывается через вызов функции, явно указанной в атрибуте android:onClick в XML.

При нажатии на любую из кнопок изменяется текстовое содержимое TextView и программно переключается состояние CheckBox.

**Листинг MainActivity.kt:**

```Kotlin
package ru.mirea.lesson1.buttonclicker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvOut: TextView
    private lateinit var btnWhoAmI: Button
    private lateinit var btnItIsNotMe: Button
    private lateinit var checkBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvOut = findViewById(R.id.tvOut)
        btnWhoAmI = findViewById(R.id.btnWhoAmI)
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe)
        checkBox = findViewById(R.id.checkBox)

        btnWhoAmI.setOnClickListener {
            tvOut.text = "Мой номер по списку № 3"
            checkBox.isChecked = true 
        }
    }

    fun onMyButtonClick(view: View) {
        tvOut.text = "Это не я сделал"
        checkBox.isChecked = false
    }
}
```

### 4. Вывод
В ходе выполнения практической работы были успешно установлены и настроены инструменты разработки (Android Studio). Были получены и закреплены практические навыки создания графических интерфейсов с использованием LinearLayout, TableLayout и ConstraintLayout. Изучен механизм работы системы ресурсов Android, в частности создание альтернативных макетов для альбомной ориентации экрана (layout-land).
Кроме того, были успешно освоены два подхода к обработке событий пользовательского ввода (нажатий на кнопки) с адаптацией под синтаксис языка Kotlin. Реализовано программное управление состоянием UI-компонентов (изменение текста и переключение состояний CheckBox) во время выполнения приложения.