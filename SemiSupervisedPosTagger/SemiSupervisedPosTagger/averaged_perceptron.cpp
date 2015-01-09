//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include <iostream>
#include <fstream>
#include "averaged_perceptron.h"
#include "assert.h"

averaged_perceptron::averaged_perceptron(const int tag_size, const int feat_size) {
    weights = new unordered_map<int, float> *[tag_size];
    averaged_weights = new unordered_map<int, float> *[tag_size];
    for (int i = 0; i < tag_size; i++) {
        weights[i] = new unordered_map<int, float>[feat_size];
        averaged_weights[i] = new unordered_map<int, float>[feat_size];
    }
    iteration = 1;
    this->tag_size=tag_size;
    this->feat_size=feat_size;
}

averaged_perceptron::averaged_perceptron(const int tag_size, const int feat_size, unordered_map<int, float> **averaged_weights) {
    weights = new unordered_map<int, float> *[tag_size];
    for (int i = 0; i < tag_size; i++)
        weights[i] = new unordered_map<int, float>[feat_size];
    iteration = 1;
    this->averaged_weights = averaged_weights;
    this->tag_size=tag_size;
    this->feat_size=feat_size;
}

averaged_perceptron *averaged_perceptron::load_model(const char *file_path) {
    ifstream reader(file_path);
    if(!reader)
        cout<<"file does not exist!"<<endl;

    int tag_size, feat_size;
    reader >> tag_size;
    reader >> feat_size;

    unordered_map<int, float>** averaged_weights = new unordered_map<int, float> *[tag_size];
    for (int i = 0; i < tag_size; i++) {
        averaged_weights[i] = new unordered_map<int, float>[feat_size];
    }

    int t_index, f_index;
    int feat;
    float value;
    while (!reader.eof()) {
        reader >> t_index;
        reader  >> f_index;
        reader>> feat;
        reader >> value;
        averaged_weights[t_index][f_index][feat]=value;
    }
    reader.close();
    return new averaged_perceptron(tag_size, feat_size,averaged_weights);
}

void averaged_perceptron::save_model(const char *file_path) {
    ofstream writer;
    writer.open(file_path,ios::binary|ios::out);
    writer<<tag_size<<endl;
    writer<<feat_size<<endl;

    for(int i=0;i<tag_size;i++) {
        for (int j = 0; j < feat_size; j++) {
            for(auto kv:weights[i][j]){
                float new_val=kv.second-(averaged_weights[i][j][kv.first]/iteration);
                if (new_val!=0.0f) {
                    writer << i << endl;
                    writer << j << endl;
                    writer << kv.first << endl;
                    writer << new_val << endl;
                }
            }
        }
    }
    writer.flush();
    writer.close();
}

void averaged_perceptron::increment_iteration() {
    iteration++;
}

void averaged_perceptron::change_weight(const int tag_index, const int feat_index, const int feature, const float change) {
	if(feature==-1)
		return;

    if(weights[tag_index][feat_index].count(feature)>0)
        weights[tag_index][feat_index][feature]=weights[tag_index][feat_index][feature]+change;
    else
        weights[tag_index][feat_index][feature]=change;

    if(averaged_weights[tag_index][feat_index].count(feature)>0)
        averaged_weights[tag_index][feat_index][feature]=averaged_weights[tag_index][feat_index][feature]+iteration*change;
    else
        averaged_weights[tag_index][feat_index][feature]=iteration*change;
}

// returns the score of the features given the tag index; arr_size is for checking the size of the array
float averaged_perceptron::score(vector<int> const features, const int tag_index, const bool is_decode) {
    float score=0;
    unordered_map<int,float>* map=is_decode?averaged_weights[tag_index]:weights[tag_index];

    for(int i=0;i<features.size();i++){
	    int feat=features.at(i);
        if(feat!=-1 && map[i].count(feat)>0)
            score+=map[i][feat];
    }

    return score;
}


unordered_map<int, float> **averaged_perceptron::get_averaged_weights() {
	unordered_map<int,float>** avg  = new unordered_map<int, float> *[tag_size];
	for (int i = 0; i < tag_size; i++) {
		avg[i] = new unordered_map<int, float>[feat_size];
	}

	for(int i=0;i<tag_size;i++){
		for(int j=0;j<feat_size;j++){
			unordered_map<int,float> w=weights[i][j];
			unordered_map<int,float> a=averaged_weights[i][j];

			for(unordered_map<int,float>::iterator iter=w.begin();iter!=w.end();++iter){
				int key=iter->first;
				float val=iter->second;
				float a_val=a[key];

				float new_val=val-(a_val/iteration);
				if(new_val!=0.0f)
					avg[i][j][key]=new_val;
			}
		}
	}
	return avg;
}

float averaged_perceptron::score(const int tag_index, const int feat_index, const int feat, const bool is_decode) {
	if(feat==-1)
		return 0;
	unordered_map<int,float>* map=is_decode?averaged_weights[tag_index]:weights[tag_index];

	if(map[feat_index].count(feat)>0)
		return map[feat_index][feat];

	return 0;
}

int averaged_perceptron::size() {
    int s=0;
    for(int i=0;i< tag_size;i++)
        for(int j=0;j<feat_size;j++)
            s+=averaged_weights[i][j].size();
    return s;
}
