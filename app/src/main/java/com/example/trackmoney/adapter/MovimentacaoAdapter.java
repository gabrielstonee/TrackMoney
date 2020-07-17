package com.example.trackmoney.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackmoney.R;
import com.example.trackmoney.model.Movimentacao;

import java.util.List;

public class MovimentacaoAdapter extends RecyclerView.Adapter<MovimentacaoAdapter.MyViewHolder> {
    private List<Movimentacao> movimentacaoList;
    Context context;
    public MovimentacaoAdapter(List<Movimentacao> movimentacaoList, Context context) {
        this.movimentacaoList = movimentacaoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler_adapter,parent, false);
        return new MyViewHolder(itemList);
        //to retornando um item de lista
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Movimentacao movimentacao = movimentacaoList.get(position);
        holder.movimentacaoDescricao.setText(movimentacao.getDescricao());
        holder.movimentacaoCategoria.setText(movimentacao.getCategoria());
        holder.movimentacaoValor.setText(String.valueOf(movimentacao.getValor()));
        if (movimentacao.getTipo().equals("d")) {
            holder.movimentacaoValor.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.movimentacaoValor.setText("-" + movimentacao.getValor());
        }
    }

    @Override
    public int getItemCount() {
        return movimentacaoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView movimentacaoDescricao, movimentacaoValor, movimentacaoCategoria;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movimentacaoDescricao = itemView.findViewById(R.id.textDescrição);
            movimentacaoValor = itemView.findViewById(R.id.textValor);
            movimentacaoCategoria = itemView.findViewById(R.id.textCategoria);
        }
    }
}
