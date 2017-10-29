#include <iostream>
#include <cmath>
#include <iomanip>
using namespace std;





int main() {
	int inn[43] = {-2.23437 ,9.39079, 18.646, 3.48209, 3.1971, 5.25959, -0.298361,
					4.70921, 2.2568, -2.89335, -3.12642, 1.68339, -7.17993, -9.49075, 
					1.36411, -0.790497, -6.00298, 1.28758, -3.10086, -2.04844, -3.92784,
					0.898868, -3.91429, -3.01022, -1.97393, -4.22979, -9.09471, -0.435965,
					-5.72374, -2.08825, -4.63809, 1.94329, -1.99705, 2.56206, -2.40959, -4.6515,
					-3.1688, 0.905529, -1.16805, -0.000413656, 1.44544, -3.85809, -3.4213};

	float out[43];
	float sum = 0;
	float max = inn[0];
	for (int i =0; i < 43; i = i+1){
		if(inn[i] > max){
			max = inn[i];
		};
	};
	for (int i =0; i < 43; i = i+1){
   		out[i] = std::exp(inn[i] - max);
   		sum = sum + out[i];
    };
    for (int i =0; i < 43; i = i+1){
   		cout << max << " -- " << sum << " -- " << out[i];
   		out[i] = out[i] / sum;
   		cout << " -------- " << out[i]  << '\n';
   		
    };
	
    return 0;
}