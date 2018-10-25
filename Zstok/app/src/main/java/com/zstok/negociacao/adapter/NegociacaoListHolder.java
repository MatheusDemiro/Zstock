package com.zstok.negociacao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zstok.R;

public class NegociacaoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public TextView tvCardViewNomeCpfEmpresa;
    public TextView tvCardViewDataInicio;
    public TextView tvCardViewDataFim;
    public View mainLayout;
    public View linearLayout;
    private NegociacaoListHolder.ClickListener itemClickListener;

    public NegociacaoListHolder(final View itemView) {
        super(itemView);

        tvCardViewNomeCpfEmpresa = itemView.findViewById(R.id.tvNomeCpfCardViewNegocicao);
        tvCardViewDataInicio = itemView.findViewById(R.id.tvDataInicioCardViewNegociacao);
        tvCardViewDataFim = itemView.findViewById(R.id.tvDataFimCardViewNegociacao);
        mainLayout = itemView.findViewById(R.id.cardViewNegociacao);
        linearLayout = itemView.findViewById(R.id.negociacaoCard);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null){
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(NegociacaoListHolder.ClickListener clickListener){
        itemClickListener = clickListener;
    }
}
