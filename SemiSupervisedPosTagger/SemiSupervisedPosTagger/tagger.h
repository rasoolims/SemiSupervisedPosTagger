//
// Created by Mohammad Sadegh Rasooli on 1/9/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __tagger_H_
#define __tagger_H_

#include <vector>
#include "string"
#include "index_maps.h"
#include "averaged_perceptron.h"
#include "sentence.h"

using namespace std;
class tagger {
public:
	static vector<string> tag(const string line,const index_maps maps,const averaged_perceptron classifier, const bool is_decode=true, const string delim="_", const bool use_beam_search=false,const int beam_size=100);

	static vector<string> tag(const sentence sen,const index_maps maps, const averaged_perceptron classifier, const bool is_decode=true, const bool use_beam_search=false,const int beam_size=100);

	static vector<int> tag(const sentence sen, const averaged_perceptron classifier, const bool is_decode=true, const bool use_beam_search=false,const int beam_size=100);


};


#endif //__tagger_H_
