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
	static vector<int> viterbi_third_order(const sentence sentence,const averaged_perceptron perceptron, bool is_decode);
};


#endif //__viterbi_H_
