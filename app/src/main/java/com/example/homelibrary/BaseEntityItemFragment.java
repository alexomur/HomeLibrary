package com.example.homelibrary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseEntityListFragment<T> extends Fragment {
    protected RecyclerView recyclerView;
    protected EntityAdapter<T> adapter;
    protected Button btnCreate, btnUpdate, btnDelete, btnRefresh;

    // Ключ для аргументов
    public static final String ARG_ENTITY_TYPE = "entity_type";
    protected EntityType entityType;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_entity_list, container, false);

        // Инициализация UI
        recyclerView = view.findViewById(R.id.recyclerAdmin);
        btnCreate   = view.findViewById(R.id.btnCreate);
        btnUpdate   = view.findViewById(R.id.btnUpdate);
        btnDelete   = view.findViewById(R.id.btnDelete);
        btnRefresh  = view.findViewById(R.id.buttonRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntityAdapter<>();
        recyclerView.setAdapter(adapter);

        // Получаем тип сущности из аргументов
        if (getArguments() != null) {
            entityType = (EntityType) getArguments().getSerializable(ARG_ENTITY_TYPE);
        }

        // Загрузка данных
        loadData();

        // Обработчик кнопки Refresh
        btnRefresh.setOnClickListener(v -> loadData());

        // TODO: обработчики Create/Update/Delete

        return view;
    }

    /** Загружает данные из БД по типу entityType и обновляет адаптер */
    protected void loadData() {
        switch (entityType) {
            case USERS:
                DBManager.getInstance().getAllUsers()
                        .addOnSuccessListener(list -> adapter.setItems((List<T>) list));
                break;
            case BOOKS:
                DBManager.getInstance().getAllBooks()
                        .addOnSuccessListener(list -> adapter.setItems((List<T>) list));
                break;
            case AUTHORS:
                DBManager.getInstance().getAllAuthors()
                        .addOnSuccessListener(list -> adapter.setItems((List<T>) list));
                break;
        }
    }

    /** Позволяет активности вызывать обновление */
    public void refresh() {
        loadData();
    }
}
