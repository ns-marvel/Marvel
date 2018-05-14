package com.springwoodcomputers.marvel.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.pojo.Character;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

import static android.support.v7.widget.RecyclerView.ViewHolder;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    @Getter
    @Setter
    private List<Character> characterList;

    private OnCharacterClickedListener listener;

    SearchResultsAdapter(OnCharacterClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        return new SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsViewHolder holder, int position) {
        holder.bind(characterList.get(position));
    }

    @Override
    public int getItemCount() {
        return characterList == null ? 0 : characterList.size();
    }

    @Override
    public long getItemId(int position) {
        return characterList.get(position).getId();
    }

    class SearchResultsViewHolder extends ViewHolder {

        @BindView(R.id.character_name)
        TextView characterName;

        @BindView(R.id.character_image)
        ImageView characterImage;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        SearchResultsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener((v) -> listener.onCharacterClicked(characterList.get(getAdapterPosition())));
        }

        void bind(Character character) {
            characterName.setText(character.getName());
            progressBar.setVisibility(VISIBLE);
            Picasso.get()
                    .load(character.getThumbnail().getImageUrl())
                    .into(characterImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(GONE);
                        }
                    });
        }
    }

    interface OnCharacterClickedListener {

        void onCharacterClicked(Character character);
    }
}