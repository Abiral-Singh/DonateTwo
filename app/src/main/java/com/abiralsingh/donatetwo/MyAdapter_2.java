package com.abiralsingh.donatetwo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class MyAdapter_2 extends FirebaseRecyclerAdapter<Donate_Item, MyAdapter_2.MyViewHolder> {

    private OnItemClickListener listener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyAdapter_2(@NonNull FirebaseRecyclerOptions<Donate_Item> options) {

        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Donate_Item model) {
        holder.textViewPost_title.setText(model.getPostTitle());
        holder.textViewPost_tag.setText(model.getTag());
        holder.textViewPost_desc.setText(model.getPostDesc()) ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent,false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPost_title;
        TextView textViewPost_tag;
        TextView textViewPost_desc;
        Button claim_donation;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewPost_title = itemView.findViewById(R.id.text_post_title);
            textViewPost_tag = itemView.findViewById(R.id.text_post_tag);
            textViewPost_desc = itemView.findViewById(R.id.text_post_desc);
            claim_donation = itemView.findViewById(R.id.button_claim_donation);
            claim_donation.setText("Remove");

            claim_donation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DataSnapshot dataSnapshot ,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
