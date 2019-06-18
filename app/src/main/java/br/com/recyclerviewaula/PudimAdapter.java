package br.com.recyclerviewaula;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class PudimAdapter extends RecyclerView.Adapter {

    private List<Pudim> listaPudims;
    private Actions actions;
    private int posicaoRemovidoRecentemente;
    private Pudim pudimRemovidoRecentemente;

    public PudimAdapter(List<Pudim> listaPudims, Actions actions) {
        this.listaPudims = listaPudims;
        this.actions = actions;
    }

    public List<Pudim> getListaPudims() {
        return listaPudims;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);

        PudimViewHolder holder = new PudimViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        final PudimViewHolder holder = (PudimViewHolder) viewHolder;
        holder.saborTextView.setText(listaPudims.get(i).getSabor());
        holder.coberturaTextView.setText(listaPudims.get(i).getCobertura());
        holder.imagemIlustrativa.setImageBitmap(listaPudims.get(i).getFoto());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.edit(viewHolder.getAdapterPosition());
//                Picasso.get().load(String.valueOf(listaPudims.get(i).getFoto())).into(holder.imagemIlustrativa);
            }
        });


    }

    public void remover(int position){
        posicaoRemovidoRecentemente = position;
        pudimRemovidoRecentemente = listaPudims.get(position);


        listaPudims.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,this.getItemCount());
        actions.undo();
    }

    public void restaurar(){
        listaPudims.add(posicaoRemovidoRecentemente, pudimRemovidoRecentemente);
        notifyItemInserted(posicaoRemovidoRecentemente);
    }

    public void inserir(Pudim pudim){
        listaPudims.add(pudim);
        notifyItemInserted(getItemCount());
    }

    public void mover(int fromPosition, int toPosition){
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(listaPudims, i, i+1);
        else
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(listaPudims, i, i-1);
        notifyItemMoved(fromPosition,toPosition);
    }

    public void updateTitle(String newSabor, int position){
        listaPudims.get(position).setSabor(newSabor);
        notifyItemChanged(position);
    }

    public void updateGenero(String newCobertura, int position){
        listaPudims.get(position).setCobertura(newCobertura);
        notifyItemChanged(position);
    }


    public void update(Pudim pudim, int position){
        listaPudims.get(position).setSabor(pudim.getSabor());
        listaPudims.get(position).setCobertura(pudim.getCobertura());
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return listaPudims.size();
    }

    public static class PudimViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemIlustrativa;
        TextView saborTextView;
        TextView coberturaTextView;

        public PudimViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            imagemIlustrativa = (ImageView) itemView.findViewById(R.id.imagemIlustrativa);
            saborTextView = (TextView) itemView.findViewById(R.id.saborTextView);
            coberturaTextView = (TextView) itemView.findViewById(R.id.coberturaTextView);
        }
    }
}
