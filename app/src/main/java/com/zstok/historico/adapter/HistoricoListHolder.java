package com.zstok.historico.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zstok.R;

public class HistoricoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public TextView tvCardViewNome;
    public TextView tvCardViewTotalCompra;
    public TextView tvCardViewDataCompra;
    public View mainLayout;
    public View linearLayout;
    private HistoricoListHolder.ClickListener itemClickListener;

    public HistoricoListHolder(final View itemView) {
        super(itemView);

        tvCardViewNome = itemView.findViewById(R.id.tvNomeCpfCardViewHistorico);
        tvCardViewTotalCompra = itemView.findViewById(R.id.tvTotalCardViewProduto);
        tvCardViewDataCompra = itemView.findViewById(R.id.tvDataCompraCardViewHistorico);
        mainLayout = itemView.findViewById(R.id.cardViewHistorico);
        linearLayout = itemView.findViewById(R.id.historicoCard);

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
    public void setOnItemClickListener(HistoricoListHolder.ClickListener clickListener){
        itemClickListener = clickListener;
    }
}
