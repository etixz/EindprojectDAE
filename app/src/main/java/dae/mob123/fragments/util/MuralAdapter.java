package dae.mob123.fragments.util;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dae.mob123.R;
import dae.mob123.model.Mural;

public class MuralAdapter extends RecyclerView.Adapter<MuralAdapter.MuralViewHolder> {

    //inner class
    class MuralViewHolder extends RecyclerView.ViewHolder{
        //verwijzingen naar componenten in cardview
        private CardView muralCV;
        private TextView artistYearMuralTV;
        private TextView characterMuralTV;
        private TextView addressMuralTV;
        private final View.OnClickListener detailListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: bundle data and put serializable
            }
        };
        public MuralViewHolder(@NonNull View cardView) {
            super(cardView);
            muralCV = cardView.findViewById(R.id.cv_mural_card);
            artistYearMuralTV = cardView.findViewById(R.id.tv_card_mural_artistyear);
            characterMuralTV = cardView.findViewById(R.id.tv_card_mural_character);
            addressMuralTV = cardView.findViewById(R.id.tv_card_mural_address);
            muralCV.setOnClickListener(detailListener);
        }
    }

    private Application mApplication;
    private List<Mural> items;

    public MuralAdapter(Application mApplication) {
        this.mApplication = mApplication;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public MuralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View muralCard = layoutInflater.inflate(R.layout.mural_card, parent, false);
        return new MuralViewHolder(muralCard);
    }

    @Override
    public void onBindViewHolder(@NonNull MuralViewHolder cardHolder, int position) {
        Mural currentMural = items.get(position);
        cardHolder.characterMuralTV.setText(currentMural.getCharacter().toUpperCase());
        cardHolder.artistYearMuralTV.setText("by " + currentMural.getArtist() + ", " + currentMural.getYear());
        cardHolder.addressMuralTV.setText(currentMural.getCoordinates().toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<Mural> murals) {
        items.clear();
        items.addAll(murals);
    }
}