package com.example.trackmoney.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.trackmoney.adapter.MovimentacaoAdapter;
import com.example.trackmoney.config.ConfigFirebase;
import com.example.trackmoney.helper.Base64Custom;
import com.example.trackmoney.model.Movimentacao;
import com.example.trackmoney.model.Usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackmoney.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;
    //o usuarioRef é pra tmb controlar o firebase pra ele só ter um evento de listener se tiver usando o app
    private DatabaseReference usuarioRef;
    private DatabaseReference movimentacaoRef;
    private ValueEventListener eventListenerUsuario, eventListenerMovimentacao;
    private FirebaseAuth autenticacao = ConfigFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();

    //recyclerview
    private RecyclerView recyclerViewList;
    private MovimentacaoAdapter movimentacaoAdapter;
    private Movimentacao movimentacaoSelecionada;
    private List<Movimentacao> movimentacaoList = new ArrayList<>();
    private String mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("trackmoney");
        setSupportActionBar(toolbar);


        calendarView = findViewById(R.id.calendarView);
        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        recyclerViewList = findViewById(R.id.recyclerMovimentos);
        configCalendario();
        swipe();
        //configurar adapter

        movimentacaoAdapter = new MovimentacaoAdapter(movimentacaoList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerViewList.setAdapter(movimentacaoAdapter);
    }

    public void swipe(){
        //esse método dá o evento de swipe
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //o getMovementFlags define como deve ser o movimento
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                //ESSE ACTION FAZ COM Q O MOVIMETNO DE DRAG AND DROP ESTEJA INATIVO
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //ISSO DEFINE QUE PODEMOS ARRASTAR TANTO NO INICIO DO ITEM E NO FINAL
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };
        //agora eu passo pro recyclerview;
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewList);
    }

    private void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir movimentação");
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacaoSelecionada = movimentacaoList.get(position);
                movimentacaoSelecionada.getKey();
                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsuario);
                movimentacaoRef = firebaseRef
                        .child("movimentacoes")
                        .child(idUsuario)
                        .child(mesAnoSelecionado)
                        .child(movimentacaoSelecionada.getKey());
                movimentacaoRef.removeValue();
                //vai remover automaticamente o nó
                movimentacaoAdapter.notifyItemRemoved(position);
                atualizarSaldo();

                Toast.makeText(PrincipalActivity.this, "Movimentação excluída", Toast.LENGTH_SHORT);
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                movimentacaoAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }
    public void atualizarSaldo(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        if(movimentacaoSelecionada.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacaoSelecionada.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }
        if(movimentacaoSelecionada.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacaoSelecionada.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mesAnoSelecionado = DateCustom.dataEscolhida(movimentacaoList.get(0).getData());
        //é bom colocar o resumo no on start e n no oncreate, pq o onstop para tudo a partir do OnStart, então, o firebase, como foi colocado no oncreate iria
        //continuar agindo mesmo depois de dado o oStop
        recuperarResumo();
        LoadList();
    }

    public void recuperarResumo(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        eventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;
                
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);
                textoSaudacao.setText("Olá, " + usuario.getNome() + "!");
                textoSaldo.setText(resultadoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadList(){
        //list tasks
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        movimentacaoRef = firebaseRef
                .child("movimentacoes")
                .child(idUsuario)
                .child(mesAnoSelecionado);
        eventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacaoList.clear();
                //o cler é para limpar a lista antes de começar a setar configurações pra ela
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    //essa chave é pra poder identificar na hora de excluir uma movimentação
                    movimentacaoList.add(movimentacao);
                    //Log.i("ATENCO", "valor: " + movimentacao.getDescricao());
                }
                Log.i("movi", "movi" + movimentacaoList.size());
                movimentacaoAdapter.notifyDataSetChanged();

                //movimentacaoAdapter.notifyDataSetChanged();
                //pra notificar que os dados foram atualizados
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //se eu tivesse configurado o recyclerview aqui ia dar errado, pois toda vez ele ia querer modificar aqui
        //configure recyclerView
    }

    @Override
    protected void onStop() {
        super.onStop();
        //esse método é chamado sempre que o app n estiver mais sendo utilizado
        Log.i("evento", "removido");
        usuarioRef.removeEventListener(eventListenerUsuario);
        movimentacaoRef.removeEventListener(eventListenerMovimentacao);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){
        Intent intent = new Intent(this, DespesasAcitivity.class);
        startActivity(intent);
    }
    public void adicionarReceita(View view){
        Intent intent = new Intent(this, ReceitasAcitivity.class);
        startActivity(intent);
    }
    public void configCalendario(){
        CharSequence meses[] = {"Jan", "Fev", "Mar", "Abr", "Maio", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        calendarView.setTitleMonths(meses);
        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth()) );//o format recebe o formato q eu quero aplicar ao numero
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear() );
        Log.i("Mesanosel", "mesanosele" + mesAnoSelecionado);
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth()) );
                mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

                movimentacaoRef.removeEventListener( eventListenerMovimentacao );
                //removemos porque antes de anexar um evento, removemos o anterior
                LoadList();
            }
        });
    }
}