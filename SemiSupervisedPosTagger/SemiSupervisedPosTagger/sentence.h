//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __Sentence_H_
#define __Sentence_H_

#include "string"
#include "index_maps.h"
#include "vector"

using namespace std;
class sentence {
	public:
		int length;
		vector<int> words;
		vector<int>  tags;

		// for at most four different lengths
		vector<int>  prefixes[4];
		vector<int>  suffixes[4];

		vector<bool>  contains_number;
		vector<bool> contains_hyphen;
		vector<bool> contains_uppercase_letter;

		vector<int> get_features(const int position, const  int prev2_tag,const int prev_tag);
		vector<int> get_emission_features(const int position);

		sentence(const vector<string> words,const vector<string> tags,const int length, const unordered_map<string,int> string_dict);
		~sentence();
};


#endif //__Sentence_H_
