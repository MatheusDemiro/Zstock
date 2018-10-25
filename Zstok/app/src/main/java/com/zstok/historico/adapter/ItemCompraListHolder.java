package com.zstok.historico.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zstok.R;

public class ItemCompraListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public ImageView imgCardViewItemCompra;
    public TextView tvCardViewNomeItemCompra;
    public TextView tvCardViewPrecoItemCompra;
    public TextView tvCardViewQuantidadeItemCompra;
    public TextView tvCardViewNomeEmpresaItemCompra;
    public View mainLayout;
    public View linearLayout;
    private ItemCompraListHolder.ClickListener itemClickListener;

    public ItemCompraListHolder(final View itemView) {
        super(itemView);

        imgCardViewItemCompra = itemView.findViewById(R.id.imgCardViewItemCompra);
        tvCardViewNomeItemCompra = itemView.findViewById(R.id.tvNomeCardViewItemCompra);
        tvCardViewPrecoItemCompra = itemView.findViewById(R.id.tvPrecoCardViewItemCompra);
        tvCardViewQuantidadeItemCompra = itemView.findViewById(R.id.tvCardViewQuantidadeItemCompra);
        tvCardViewNomeEmpresaItemCompra = itemView.findViewById(R.id.tvNomeEmpresaCardViewItemCompra);
        mainLayout = itemView.findViewById(R.id.cardViewItemCompra);
        linearLayout = itemView.findViewById(R.id.itemCard);

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
    public void setOnItemClickListener(ItemCompraListHolder.ClickListener clickListener){
        itemClickListener = clickListener;
    }
}
