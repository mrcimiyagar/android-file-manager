package kasper.android.file_explorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.adapters.DocAdapter;
import kasper.android.file_explorer.listeners.OnAdapterRequestListener;
import kasper.android.file_explorer.listeners.OnDocsLoadedListener;
import kasper.android.file_explorer.models.files.Doc;
import kasper.android.file_explorer.tasks.DocsLoadTask;
import kasper.android.file_explorer.ui_utils.DocDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocsFragment extends Fragment {

    public DocsFragment() {

    }

    private String docType;
    public DocsFragment setDocType(String docType) {
        this.docType = docType;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_docs, container, false);

        final RecyclerView recyclerView = contentView.findViewById(R.id.fragment_docs_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));

        new DocsLoadTask(docType, new OnDocsLoadedListener() {
            @Override
            public void docsLoaded(ArrayList<Doc> docs) {
                recyclerView.setAdapter(new DocAdapter(docs, new OnAdapterRequestListener() {
                    @Override
                    public void scrollToTop() {

                    }
                }));
            }
        }).execute();

        return contentView;
    }
}
