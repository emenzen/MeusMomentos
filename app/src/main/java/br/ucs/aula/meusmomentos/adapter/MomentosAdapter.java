package br.ucs.aula.meusmomentos.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.ucs.aula.meusmomentos.R;
import br.ucs.aula.meusmomentos.activity.EditarMomentoActivity;
import br.ucs.aula.meusmomentos.model.Momento;

public class MomentosAdapter extends RecyclerView.Adapter<MomentosAdapter.ViewHolder> {
    private final List<Momento> elementos;

    public MomentosAdapter(List<Momento> elementos) {
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linha,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(elementos.get(position));
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtDescricao;
        private TextView txtData;
        private TextView txtLocal;
        private ImageView imagem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtData = itemView.findViewById(R.id.txtData);
            txtLocal = itemView.findViewById(R.id.txtLocal);
            imagem = itemView.findViewById(R.id.lvMomento);
        }

        private void setData(Momento momento) {
            //se não tiver título, mostra o caminho da momento..
            //txtTitulo.setText(momento.getTitulo().isEmpty()?momento.getCaminho():momento.getTitulo());
            txtDescricao.setText(momento.getDescricao());
            //Bitmap bitmap = BitmapFactory.decodeFile(momento.getCaminho());
            Bitmap bitmap = ThumbnailUtils.createImageThumbnail(momento.getCaminho(), MediaStore.Images.Thumbnails.MICRO_KIND);
            imagem.setImageBitmap(bitmap);
        }

        public void onClick(View view) {
            AppCompatActivity appCompatActivity = new AppCompatActivity();
            //Toast.makeText(view.getContext(),"Você selecionou " + elementos.get(getLayoutPosition()).getCodigo(),Toast.LENGTH_LONG).show();

            Context context = view.getContext();
            Intent intent = new Intent(context, EditarMomentoActivity.class);
            intent.putExtra("CODIGO", elementos.get(getLayoutPosition()).getCodigo());
            context.startActivity(intent);

        }
    }
}
