package com.hitec.directions.views.directions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hitec.directions.R;

import java.util.List;

public class RecyclerViewAdapterDirections extends RecyclerView.Adapter<RecyclerViewAdapterDirections.ViewHolder> {
    List<String> instructions;

    public RecyclerViewAdapterDirections(){}

    public RecyclerViewAdapterDirections(List<String> instructions) {
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.instruction.setText(instruction);

    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    /*Click events & interface*/
    //TODO
    /*Click events & interface*/

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView instruction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            instruction = itemView.findViewById(R.id.direction);
        }
    }
}
