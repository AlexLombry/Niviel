package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;

public class RankingChooseCubeDialog extends DialogFragment {

    /**
     *  Paramètre pour stocker les items selectionnées du dialog.
     */
    public static final String SELECTED_ITEMS = "selected_items";

    /**
     * Le listener du Dialog lors de sa validation
     */
    RankingChooseCubeListener listener;

    /**
     * Tableau contenant les items selectionnés
     */
    private boolean[] selectedItems = getDefaultSelectedItems();

    /**
     * Constructeur du dialog
     * @param selectedItems Tableau contenant les items selectionés
     * @return Instance du dialogue
     */
    public static RankingChooseCubeDialog newInstance(boolean[] selectedItems) {

        Bundle args = new Bundle();
        args.putBooleanArray(SELECTED_ITEMS, selectedItems);

        RankingChooseCubeDialog instance = new RankingChooseCubeDialog();
        instance.setArguments(args);
        return instance;
    }

    /**
     * Lors de la création du fragment
     * @param savedInstanceState sauvegarde
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedItems = getArguments().getBooleanArray(SELECTED_ITEMS);
    }


    /**
     * Lors de la création du dialogue
     * @param savedInstanceState sauvegarde
     * @return le dialogue
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.switch_cube);
        builder.setIcon(R.drawable.ic_swap);

        builder.setMultiChoiceItems(R.array.cubes, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                selectedItems[position] = isChecked;
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(selectedItems);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    /**
     * Change de listener pour écouter la validation du dialogue
     * @param listener listener lors de la validation
     */
    public void setListener(RankingChooseCubeListener listener) {
        this.listener = listener;
    }

    /**
     * Interface du listener
     */
    public interface RankingChooseCubeListener {
        void onClick(boolean[] selectedItems);
    }

    /**
     * Lors de la sauvegarde du dialogue
     * @param outState sauvegarde
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }

    /**
     * Retourne le tableau des items selectionnés par défaut
     * Par defaut, rien n'est selectionné, tout est a false
     * Le tableau posséde 18 élements
     * @return tableau des élements selectionnés
     */
    public static boolean[] getDefaultSelectedItems() {
        return new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    }

}
