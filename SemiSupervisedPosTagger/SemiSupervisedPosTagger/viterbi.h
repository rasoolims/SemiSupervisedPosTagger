//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __viterbi_H_
#define __viterbi_H_

#include "sentence.h"
#include "averaged_perceptron.h"

class viterbi {
public:
	static vector<int> viterbi_third_order(sentence sentence,averaged_perceptron perceptron,const int tag_size,const int feat_size,bool is_decode);
};


#endif //__viterbi_H_
