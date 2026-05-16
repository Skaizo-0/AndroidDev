package ru.mirea.mvolobueva.mireaproject.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mirea.mvolobueva.mireaproject.databinding.FragmentReflowBinding
/*


Отображает текст, который управляется через ViewModel.

ПОДРОБНОЕ ОПИСАНИЕ:
Этот фрагмент является частью архитектурного паттерна MVVM, где:
- View (Fragment) отвечает только за отображение UI
- ViewModel (ReflowViewModel) содержит бизнес-логику и данные


Фрагмент использует Data Binding для доступа к View и LiveData для
наблюдения за изменениями данных в ViewModel. Когда данные в ViewModel
меняются, фрагмент автоматически обновляет TextView.


*/

class ReflowFragment : Fragment() {
    // ПОЛЯ КЛАССА И DATA BINDING
    private var _binding: FragmentReflowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  ПОЛУЧЕНИЕ VIEWMODEL
        val reflowViewModel =
            ViewModelProvider(this).get(ReflowViewModel::class.java)
        //СОЗДАНИЕ DATA BINDING
        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //  НАХОДИМ TEXTVIEW
        val textView: TextView = binding.textReflow
        reflowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}