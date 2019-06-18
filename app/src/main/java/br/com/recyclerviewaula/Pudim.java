package br.com.recyclerviewaula;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.WebStorage;

import java.io.Serializable;

public class Pudim implements Parcelable {

    private String sabor;
    private String cobertura;
    private Bitmap foto;


    public Pudim() {

    }

    public Pudim(String sabor, String cobertura) {
        this.sabor = sabor;
        this.cobertura = cobertura;
        this.foto = foto;
    }


    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public Bitmap getFoto() {
        return foto;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sabor);
        dest.writeString(this.cobertura);
        dest.writeValue(this.foto);
    }

    public void readFromParcel(Parcel origin) {
        this.sabor = origin.readString();
        this.cobertura = origin.readString();
        this.foto = (Bitmap) origin.readValue(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<Pudim> CREATOR = new Parcelable.Creator<Pudim>() {
        @Override
        public Pudim createFromParcel(Parcel p) {
            Pudim p1 = new Pudim();
            p1.readFromParcel(p);

            return p1;
        }

        @Override
        public Pudim[] newArray(int size) {

            return new Pudim[size];
        }
    };
}