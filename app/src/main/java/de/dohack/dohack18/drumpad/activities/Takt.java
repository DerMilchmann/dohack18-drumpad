package de.dohack.dohack18.drumpad.activities;

import android.provider.ContactsContract;

public class Takt
{
    //Ein Takt wird metrisch gemessen
    //Bei einem 4/4 Takt, werden 4 Schläge mit jeweils Viertellänge geschlagen
    //Bei einem 5/8 Takt werden 5 Schläge mit jeweils Achtellänge geschlagen
    //Nach diesen Schlägen ist der Takt vorbei
    //Wie lang ein Takt nun in echter Zeit ist, wird bestimmt von den bpm, Beats per Minute
    //Beats per Minute sind Viertelschläge pro Minute
    //Ein Achtelschlag ist halb so lang wie ein Viertelschlag etc.
    int anzahlGrundschlaege;
    int Notenlaenge;
    double schlaglaenge;
    int bpm;

    public Takt(int anzahlGrundschlaege, int Notenlaenge)
    {
        this.anzahlGrundschlaege = anzahlGrundschlaege;
        this.Notenlaenge = Notenlaenge;
        bpm = 60; //Ausgabe der BPM als Metronom als Viertel
        updateSchlaglaenge();
    }

    public void incrementNotenlänge()
    {
        if(Notenlaenge > 16)
        {
            Notenlaenge = 4;
        }else
        {
            Notenlaenge *= 2;
        }
        updateSchlaglaenge();
    }
    public void incrementGrundschlaege()
    {
        if(anzahlGrundschlaege == Notenlaenge)
        {
            anzahlGrundschlaege = 2;
        }else
        {
            anzahlGrundschlaege++;
        }
        updateSchlaglaenge();
    }

    public void updateSchlaglaenge() //in Millisekunden
    {
        schlaglaenge = bpm / 60 * 1000 * 4 / Notenlaenge;
    }

    public double getSchlaglaenge()
    {
        return schlaglaenge;
    }

    public int getAnzahlGrundschlaege() {
        return anzahlGrundschlaege;
    }

    public int getNotenlaenge() {
        return Notenlaenge;
    }

    public int getBpm() {
        return bpm;
    }
}
