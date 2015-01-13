//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __unicode_manager_H_
#define __unicode_manager_H_

#include "string"
#include "vector"

using namespace std;
class unicode_manager {
	public:
		// get prefixes at most up to length len
		static vector<string> prefixes(const string word, const int len);

		// get prefixes at most up to length len
		static vector<string> suffixes(const string word, const int len);
};


#endif //__unicode_manager_H_
