//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __AveragedPerceptron_H_
#define __AveragedPerceptron_H_

#include <unordered_map>

using namespace std;

class AveragedPerceptron {
private:
    /**
    a two-dimensional map for the features
    the key for the features are long (hashed values from strings)
    the value is a floating point
    the first dimension is the tag_size and the second dimension is the feat_size
    **/
    unordered_map<long, float>** weights;

    unordered_map<long, float>** averaged_weights;

    int tag_size;
    int feat_size;

public:
    int iteration;

    AveragedPerceptron(const int tag_size, const int feat_size);

    AveragedPerceptron(const int tag_size, const int feat_size, unordered_map<long, float> **averaged_weights);

    ~AveragedPerceptron(){};

    static AveragedPerceptron* load_model(const char *file_path);

    void save_model(const char *file_path);

    void increment_iteration();

    void change_weight(const int tag_index, const int feat_index, const long feature, const float change);

    float score(const long features[],const int arr_size, const int tag_index, const bool is_decode);

    int size();
};


#endif //__AveragedPerceptron_H_
