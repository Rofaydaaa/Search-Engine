package Indexer;

import CostumDataStructures.*;
import DataBase.DataBaseManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Indexer {

    //url data to be indexed
    URLData currentUrlData;
    DataBaseManager dbManager;
    Trie stoppingWord;
    String stoppingWordDictionaryFilePath;
    Indexer(URLData urlD, DataBaseManager db){

        //build stopping word tries for fast search
        buildStoppingWord();

        //assign data members
        this.dbManager = db;
        this.currentUrlData = urlD;
    }

    public void buildStoppingWord(){
        stoppingWord = new Trie();
        stoppingWordDictionaryFilePath = "stoppingWords.txt";

        try (FileReader fileReader = new FileReader(stoppingWordDictionaryFilePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String word;
            while ((word = bufferedReader.readLine()) != null) {
                stoppingWord.insert(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
