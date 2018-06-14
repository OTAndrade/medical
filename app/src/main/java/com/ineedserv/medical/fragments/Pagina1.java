package com.ineedserv.medical.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ineedserv.medical.MainActivity;
import com.ineedserv.medical.R;


public class Pagina1 extends Fragment {

	TextView textView;
	String texto;

	public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.pagina1, container, false);
		textView = (TextView) view.findViewById(R.id.textView1);
		MainActivity activity = (MainActivity) getActivity();
		if (activity.getAyuda().equals("ayudaSolicitante")){
			texto = getResources().getString(R.string.ayudaSolicitante);
			textView.setText(texto);
		} else {
			texto = getResources().getString(R.string.ayudaOfertante);
			textView.setText(texto);
		}

		return  view;
	}



}