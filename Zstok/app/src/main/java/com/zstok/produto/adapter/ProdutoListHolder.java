package com.zstok.produto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zstok.R;


public class ProdutoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public ImageView imgCardViewProduto;
    public TextView tvCardViewNomeProduto;
    public TextView tvCardViewPrecoProduto;
    public TextView tvCardViewQuantidadeEstoque;
    public TextView tvCardViewNomeEmpresa;
    public View mainLayout;
    public View linearLayout;
    private ProdutoListHolder.ClickListener itemClickListener;

    public ProdutoListHolder(final View itemView) {
        super(itemView);

        imgCardViewProduto = itemView.findViewById(R.id.imgCardViewProduto);
        tvCardViewNomeProduto = itemView.findViewById(R.id.tvNomeCardViewProduto);
        tvCardViewPrecoProduto = itemView.findViewById(R.id.tvPrecoCardViewProduto);
        tvCardViewQuantidadeEstoque = itemView.findViewById(R.id.tvQuantidadeCardViewProduto);
        tvCardViewNomeEmpresa = itemView.findViewById(R.id.tvNomeEmpresaCardViewProduto);
        mainLayout = itemView.findViewById(R.id.cardViewProduto);
        linearLayout = itemView.findViewById(R.id.produtoCard);

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
    public void setOnItemClickListener(ProdutoListHolder.ClickListener clickListener){
        itemClickListener = clickListener;
    }
}