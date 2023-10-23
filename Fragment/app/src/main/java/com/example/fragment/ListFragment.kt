package com.example.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fragment.databinding.FragmentListBinding

class ListFragment : Fragment() {

    // main과 연결해야 하므로 main을 우선 불러온다
    var mainActivity: MainActivity? = null

    // binding 바깥으로 뺀다
    lateinit var binding: FragmentListBinding

    // 액티비티에서 요청 시 뷰를 만들어서 보여준다!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_list, container, false)

        // 뷰를 바로 리턴하는 것이 아닌
        // 바인딩해서 버튼에 리스너 등록 후에 바인딩 리턴
        // viewBinding = true 안하면 안뜬다
        binding = FragmentListBinding.inflate(inflater, container, false)



        binding.btnNext.setOnClickListener{mainActivity?.goDetail()}

        // 값을 꺼내온다
        binding.textTitle.text = arguments?.getString("key1")

        // int -> String 으로 변환 -> 문자열 템플릿 이용
        binding.textValue.text = "${arguments?.getInt("key2")}"

        return binding.root

    }

    // main을 받을때 생명주기 중에서 onAttach가 적합하다.
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is MainActivity)
            mainActivity = context
    }

    // 전달 받은 문자열을 출력한다
    fun setValue(value: String){
        binding.textFromActivity.text = value
    }
}