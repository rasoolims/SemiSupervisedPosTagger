//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __IndexMaps_H_
#define __IndexMaps_H_

#include "string"
#include "unordered_map"
#include "vector"

using namespace std;
class index_maps {
	public:
		unordered_map<string,int>  string_dic;
		vector<string> reverse_dict;

		index_maps(unordered_map<string,int>  string_dic,vector<string> reverse_dict);
		~index_maps();

};


#endif //__IndexMaps_H_
