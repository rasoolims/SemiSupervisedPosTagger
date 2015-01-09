//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#ifndef __AveragedPerceptron_H_
#define __AveragedPerceptron_H_

#include <unordered_map>
#include "vector"

using namespace std;
class averaged_perceptron {
private:
    /**
    a two-dimensional map for the features
    the key for the features are int (hashed values from strings)
    the value is a floating point
    the first dimension is the tag_size and the second dimension is the feat_size
    **/
    unordered_map<int, float>** weights;

    unordered_map<int, float>** averaged_weights;

    int tag_size;

public:
    int iteration;
	int feat_size;

	averaged_perceptron(const int tag_size, const int feat_size);

    averaged_perceptron(const int tag_size, const int feat_size, unordered_map<int, float> **averaged_weights);

    ~averaged_perceptron(){};

    static averaged_perceptron * load_model(const char *file_path);

    void save_model(const char *file_path);

    void increment_iteration();

    void change_weight(const int tag_index, const int feat_index, const int feature, const float change);

    float score(const vector<int> features, const int tag_index, const bool is_decode);

	float score(const int tag_index, const int feat_index, const int feat, const bool is_decode);

	int size();
};


#endif //__AveragedPerceptron_H_
