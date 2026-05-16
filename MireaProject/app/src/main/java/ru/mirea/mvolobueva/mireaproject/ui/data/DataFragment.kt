package ru.mirea.mvolobueva.mireaproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
/*

КРАТКОЕ ОПИСАНИЕ:
Базовый фрагмент для отображения информации/данных
заготовка  для будущего функционала работы с данными

ПОДРОБНОЕ ОПИСАНИЕ:
Этот фрагмент представляет собой минимальную реализацию, которая только
загружае layout-файл fragment_data.xml.
*/
class DataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }
}