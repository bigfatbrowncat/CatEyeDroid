#include <com/cateye/procedures/compressor/CompressorStageOperationProcessor.h>
#include <colorlib.h>
#include <bitmaps.h>
#include <jni.h>
#include <math.h>
#include <mem.h>
#include <time.h>
#include <pthread.h>
#include <stdio.h>

#include <vector>

using namespace std;

#define DEBUG_INFO //printf("%d\n", __LINE__);fflush(stdout);

#define JCLASS_PROGRESS_LISTENER								"com/cateye/core/ProgressListener"
#define JCLASS_PROGRESS_LISTENER_REPORT_PROGRESS				"reportProgress"
#define JCLASS_PROGRESS_LISTENER_REPORT_PROGRESS_SIGNATURE		"(F)Z"

struct Compress_progressListenerData;
typedef bool progressReporter(void* callerData, float progress);

struct innerProgressListenerData
{
	Compress_progressListenerData* compressData;
	progressReporter* outerReporter;
	float start;
	float width;

	innerProgressListenerData(Compress_progressListenerData* compressData, progressReporter* outerReporter, float start, float width) :
		compressData(compressData), outerReporter(outerReporter), start(start), width(width)
	{}
};

bool innerProgressReporter(void* callerData, float progress)
{
	DEBUG_INFO
	innerProgressListenerData& data = *(innerProgressListenerData*)callerData;

	DEBUG_INFO
	bool res = (*data.outerReporter)(data.compressData, data.start + data.width * progress);

	DEBUG_INFO
	return res;
}

template <typename T> struct counted_link
{
	T* link;
	int counter;
	counted_link<T>() : counter(0) {}
};

template <typename T> class arr2
{
private:
	counted_link<T>* data;
	int width, height;
public:
	int getWidth() const { return width; }
	int getHeight() const { return height; }

	T* getData()
	{
		return data->link;
	}

	arr2<T> clone()
	{
		return arr2<T>(this->data->link, this->width, this->height);
	}

	inline const T& operator () (int i, int j) const
	{
#ifdef DEBUG_ARRAYS
		if (i >= width)
		{
			DEBUG_INFO
			throw 1;
		}
		if (j >= height)
		{
			DEBUG_INFO
			throw 1;
		}

		if (i < 0)
		{
			DEBUG_INFO
			throw 1;
		}
		if (j < 0)
		{
			DEBUG_INFO
			throw 1;
		}
#endif
		return data->link[j * width + i];
	}
	inline T& operator () (int i, int j)
	{
#ifdef DEBUG_ARRAYS
		if (i >= width)
		{
			DEBUG_INFO
			throw 1;
		}
		if (j >= height)
		{
			DEBUG_INFO
			throw 1;
		}

		if (i < 0)
		{
			DEBUG_INFO
			throw 1;
		}
		if (j < 0)
		{
			DEBUG_INFO
			throw 1;
		}
#endif
		return data->link[j * width + i];
	}

	arr2<T>(const arr2<T>& other)
	{
		width = other.width;
		height = other.height;
		data = other.data;
#ifdef DEBUG_ARRAYS
		if (this->data == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
#endif
		data->counter ++;
	}

	arr2<T>& operator = (const arr2<T>& other)
	{
		if (this != &other)
		{
			data->counter --;
#ifdef DEBUG_ARRAYS
			if (data == NULL || data->link == NULL)
			{
				DEBUG_INFO
				throw 1;
			}
#endif
			if (data != NULL && data->counter == 0)
			{
				if (data->link != NULL) delete [] data->link;
				delete data;
			}

			width = other.width;
			height = other.height;
			data = other.data;
#ifdef DEBUG_ARRAYS
			if (this->data == NULL)
			{
				DEBUG_INFO
				throw 1;
			}
#endif
			data->counter ++;
		}
		return *this;
	}

	arr2(T* src_data, int width, int height) : width(width), height(height)
	{
		this->data = new counted_link<T>;
#ifdef DEBUG_ARRAYS
		if (this->data == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
#endif
		this->data->link = new T[width * height];
		if (this->data->link == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
		memcpy(this->data->link, src_data, width * height * sizeof(T));
		data->counter ++;
	}

	arr2(int width, int height) : width(width), height(height)
	{
		this->data = new counted_link<T>;
#ifdef DEBUG_ARRAYS
		if (this->data == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
#endif
		DEBUG_INFO
		this->data->link = new T[width * height];
		if (this->data->link == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
		DEBUG_INFO
		data->counter ++;
	}
	virtual ~arr2()
	{
		data->counter --;
#ifdef DEBUG_ARRAYS
		if (data == NULL || data->link == NULL)
		{
			DEBUG_INFO
			throw 1;
		}
#endif

		if (data != NULL && data->counter == 0)
		{
			if (data->link != NULL) delete [] data->link;
			delete data;
		}
	}
};

arr2<float> Upsample2(const arr2<float> Q, int new_w, int new_h)
{
	int w = Q.getWidth(), h = Q.getHeight();

	// Scaling
	arr2<float> Q2(new_w, new_h);
	for (int i = 0; i < new_w; i++)
	for (int j = 0; j < new_h; j++)
	{
		int i2 = i;
		if (i2 / 2 >= w) i2 = 2 * w - 1;
		int j2 = j;
		if (j2 / 2 >= h) j2 = 2 * h - 1;

		Q2(i, j) = Q(i2 / 2, j2 / 2);
	}

	// Blurring
	arr2<float> Q22(new_w, new_h);
	for (int i = 0; i < new_w; i++)
	for (int j = 0; j < new_h; j++)
	{
		Q22(i, j) = 0;
		float diver = 0;

		float mix_k = 0.5;

		if (i > 0 && j > 0)
		{
			diver += 0.125f * mix_k;
			Q22(i, j) += 0.125f * mix_k * Q2(i - 1, j - 1);
		}

		if (i > 0 && j < new_h - 1)
		{
			diver += 0.125f * mix_k;
			Q22(i, j) += 0.125f * mix_k * Q2(i - 1, j + 1);
		}

		if (i < new_w - 1 && j > 0)
		{
			diver += 0.125f * mix_k;
			Q22(i, j) += 0.125f * mix_k * Q2(i + 1, j - 1);
		}

		if (i < new_w - 1 && j < new_h - 1)
		{
			diver += 0.125f * mix_k;
			Q22(i, j) += 0.125f * mix_k * Q2(i + 1, j + 1);
		}

		if (i > 0)
		{
			diver += 0.25f * mix_k;
			Q22(i, j) += 0.25f * mix_k * Q2(i - 1, j);
		}

		if (i < new_w - 1)
		{
			diver += 0.25f * mix_k;
			Q22(i, j) += 0.25f * mix_k * Q2(i + 1, j);
		}

		if (j > 0)
		{
			diver += 0.25f * mix_k;
			Q22(i, j) += 0.25f * mix_k * Q2(i, j - 1);
		}

		if (j < new_h - 1)
		{
			diver += 0.25f * mix_k;
			Q22(i, j) += 0.25f * mix_k * Q2(i, j + 1);
		}

		diver += 1;
		Q22(i, j) += Q2(i, j);

		Q22(i, j) /= diver;
	}

	return Q22;
}

arr2<float> BuildPhi(arr2<float> H, double alpha, double beta, double noise_gate)
{
	int Hw = H.getWidth();
	int Hh = H.getHeight();

	DEBUG_INFO

	vector<int> ww, hh;
	int divides = 0, wt = Hw, ht = Hh;
	ww.push_back(wt); hh.push_back(ht);
	while (wt > 1 && ht > 1 && divides < 5)
	{
		wt /= 2; ht /= 2;
		ww.push_back(wt); hh.push_back(ht);
		divides ++;
	}

	DEBUG_INFO

	// Building H0
	arr2<float> H_cur = H.clone();/* (Hw, Hh);
	for (int i = 0; i < Hw; i++)
	for (int j = 0; j < Hh; j++)
	{
		H_cur(i, j) = H(i, j);
	}*/

	DEBUG_INFO

	// Building phi_k
	vector<arr2<float> > phi;
	for (int k = 0; k <= divides; k++)		// k is the index of H_cur
	{
		DEBUG_INFO

		float avg_grad = 0;

		int w = (int)(Hw / pow(2, k));
		int h = (int)(Hh / pow(2, k));

		if (k > 0)
		{
			DEBUG_INFO
			// Calculating the new H_cur
			arr2<float> H_cur_new(w, h);
			DEBUG_INFO
			for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
			{
				H_cur_new(i, j) = (float)(0.25 * (H_cur(2 * i, 2 * j)     + H_cur(2 * i + 1, 2 * j) +
				                                  H_cur(2 * i, 2 * j + 1) + H_cur(2 * i + 1, 2 * j + 1)));

			}
			DEBUG_INFO
			H_cur = H_cur_new;
		}
		DEBUG_INFO

		// Calculating grad_H_cur
		arr2<float> grad_H_cur_x(w, h), grad_H_cur_y(w, h);
		for (int i = 0; i < w; i++)
		for (int j = 0; j < h; j++)
		{
			if (w == 1 || h == 1)
			{
				grad_H_cur_x(i, j) = 0;
				grad_H_cur_y(i, j) = 0;
			}
			else
			{
				if (i < w - 1 && i > 0)
					grad_H_cur_x(i, j) = (float)((H_cur(i + 1, j) - H_cur(i - 1, j)) / pow(2, k + 1));
				else if (i == 0)
					grad_H_cur_x(i, j) = (float)((H_cur(i + 1, j) - H_cur(i, j)) / pow(2, k));
				else
					grad_H_cur_x(i, j) = (float)((H_cur(i, j) - H_cur(i - 1, j)) / pow(2, k));

				if (j < h - 1 && j > 0)
					grad_H_cur_y(i, j) = (float)((H_cur(i, j + 1) - H_cur(i, j - 1)) / pow(2, k + 1));
				else if (j == 0)
					grad_H_cur_y(i, j) = (float)((H_cur(i, j + 1) - H_cur(i, j)) / pow(2, k));
				else
					grad_H_cur_y(i, j) = (float)((H_cur(i, j) - H_cur(i, j - 1)) / pow(2, k));

				avg_grad += (float)sqrt(grad_H_cur_x(i, j) * grad_H_cur_x(i, j) +
				                        grad_H_cur_y(i, j) * grad_H_cur_y(i, j));
			}
		}
		DEBUG_INFO

		// Calculating phi_k
		avg_grad /= w * h;
		printf("avg_grad = %f\n", avg_grad);
		fflush(stdout);
		arr2<float> phi_k(w, h);
		for (int i = 0; i < w; i++)
		for (int j = 0; j < h; j++)
		{
			double abs_grad_H_cur = sqrt(grad_H_cur_x(i, j) * grad_H_cur_x(i, j) +
			                             grad_H_cur_y(i, j) * grad_H_cur_y(i, j));
			abs_grad_H_cur += 0.001;		// Avoiding zero

			phi_k(i, j) = (float)(pow(abs_grad_H_cur / alpha, beta - 1));

			// nOISE GATE
			float nf_edge = (float)noise_gate * avg_grad + 0.00001;
			phi_k(i, j) *= (float)(1.0 - exp(-pow(abs_grad_H_cur / nf_edge, 0.8)));
		}
		phi.push_back(phi_k);
		DEBUG_INFO
	}
	DEBUG_INFO

	// Building Phi from phi_k
	arr2<float> Phi(phi[phi.size() - 1].getWidth(), phi[phi.size() - 1].getHeight());
	for (int i = 0; i < Phi.getWidth(); i++)
	for (int j = 0; j < Phi.getHeight(); j++)
	{
		Phi(i, j) = 1;
	}
	DEBUG_INFO

	for (int k = divides; k >= 0; k--)
	{
		DEBUG_INFO
		int w = phi[k].getWidth();
		int h = phi[k].getHeight();
		// Multiplying
		DEBUG_INFO
		for (int i = 0; i < w; i++)
		for (int j = 0; j < h; j++)
		{
			//printf("i = %d, j = %d, w = %d, h = %d\n", i, j, w, h);fflush(stdout);
			Phi(i, j) *= phi[k](i, j);
		}

		if (k > 0)
		{
			Phi = Upsample2(Phi, ww[k - 1], hh[k - 1]);
		}
	}
	DEBUG_INFO

	// Extracting the correct size
	//arr2<float> Phi_cut = Phi.clone();
	/*(Hw, Hh);
	for (int i = 0; i < Hw; i++)
	for (int j = 0; j < Hh; j++)
	{
		Phi_cut(i, j) = Phi(i, j);
	}*/
	DEBUG_INFO

	return Phi;
}

void* PoissonNeimanThread_start(void* data);

class PoissonNeimanThread
{
	friend void* PoissonNeimanThread_start(void* data);
private:
	int i1, i2, w, h;
	arr2<float>& I, Inew, rho;

	pthread_t thread;

public:
	double my_delta;

	PoissonNeimanThread(int i1, int i2, int w, int h,
	                    arr2<float>& I, arr2<float>& Inew, arr2<float>& rho) :
	                    i1(i1), i2(i2), w(w), h(h), I(I), Inew(Inew), rho(rho)
	{
		pthread_create(&thread, NULL, PoissonNeimanThread_start, this);
	}

	void join()
	{
		void* res;
		pthread_join(thread, &res);
	}
};

void* PoissonNeimanThread_start(void* data)
{
	PoissonNeimanThread* thr = (PoissonNeimanThread*)data;
	int w = thr->w;
	int h = thr->h;
	int i1 = thr->i1;
	int i2 = thr->i2;
	arr2<float>& I = thr->I;
	arr2<float>& Inew = thr->Inew;
	arr2<float>& rho = thr->rho;
	double my_delta = 0;

	for (int i = i1; i < i2; i++) //	for (int i = 1; i < w + 1; i++)
	{
		// Run, Thomas, run!
		float alpha[h + 3];
		float beta[h + 3];

		alpha[1] = 0.25f; beta[1] = 0.25f * (I(i + 1, 0) + Inew(i - 1, 0));

		float* cur_alpha = &alpha[1];
		float* cur_beta = &beta[1];

		/*float *I_p1 = &(I(i + 1, 1));
		float *Inew_m1 = &(Inew(i - 1, 1));
		float *rho_m1m1 = &(rho(i - 1, 0));*/

		for (int j = 1; j < h + 1; j++)
		{
			float Fj = I(i + 1, j) + Inew(i - 1, j) - 2 * rho(i - 1, j - 1);
			/*float Fj = (*I_p1) + (*Inew_m1) - 2 * (*rho_m1m1);
			I_p1 += w + 2;
			Inew_m1 += w + 2;
			rho_m1m1 += w;*/

			float alpha_new = 1.0f / (4 - *cur_alpha);
			float beta_new = (Fj + *cur_beta) / (4.0f - *cur_alpha);

			cur_alpha++; cur_beta++;

			*cur_alpha = alpha_new;
			*cur_beta = beta_new;
		}

		// ...and the last one
		float Fj = I(i + 1, h + 1) + Inew(i - 1, h + 1);

		alpha[h + 2] = 1.0f / (4 - *cur_alpha);
		beta[h + 2] = (Fj + *cur_beta) / (4.0f - *cur_alpha);


		Inew(i, h + 1) = beta[h + 2];

		for (int j = h; j >= 0; j--)
		{
			double Iold_ij = I(i, j);
			float Inew_ij = alpha[j + 1] * Inew(i, j + 1) + beta[j + 1];
			Inew(i, j) = Inew_ij;
			my_delta += (float)fabs(Inew_ij - Iold_ij);
		}
	}

	thr->my_delta = my_delta;
}


bool SolvePoissonNeiman(arr2<float> I0, arr2<float> rho, int steps_max, float stop_dpd, progressReporter* reporter, void* reporterCallerData)
{
	DEBUG_INFO
	int w = rho.getWidth(), h = rho.getHeight();
	arr2<float> I(w + 2, h + 2);
	arr2<float> Inew(w + 2, h + 2);

	DEBUG_INFO
	// Setting initial values
	for (int i = 0; i < w + 2; i++)
	for (int j = 0; j < h + 2; j++)
	{
		int i1 = i;
		if (i == 0) i1 = 1;
		if (i == w + 1) i1 = w;
		int j1 = j;
		if (j == 0) j1 = 1;
		if (j == h + 1) j1 = h;

		I(i, j) = I0(i1 - 1, j1 - 1);
		Inew(i, j) = I0(i1 - 1, j1 - 1);
	}
	DEBUG_INFO

	int threads_num = 16;
	PoissonNeimanThread** pnthrs = new PoissonNeimanThread*[threads_num];

	float progress_max = 0;

	float delta = 0; float delta_prev = 0;
	for (int step = 0; step < steps_max; step ++)
	{
		// *** Horizontal iterations ***

		for (int q = 0; q < threads_num; q++)
		{
			int i1 = (w / threads_num) * q + 1, i2;
			if (q < threads_num - 1)
			{
				i2 = (w / threads_num) * (q + 1) + 1;
			}
			else
			{
				i2 = w + 1;
			}

			pnthrs[q] = new PoissonNeimanThread(i1, i2, w, h, I, Inew, rho);
		}

		for (int q = 0; q < threads_num; q++)
		{
			pnthrs[q]->join();
			delta += pnthrs[q]->my_delta;
			delete pnthrs[q];
		}

		// Restoring Neiman boundary conditions after horizontal iterations
		for (int i = 0; i < w + 2; i++)
		{
			Inew(i, 0) = Inew(i, 1);
			Inew(i, h + 1) = Inew(i, h);
		}
		for (int j = 0; j < h + 2; j++)
		{
			Inew(0, j) = Inew(1, j);
			Inew(w + 1, j) = Inew(w, j);
		}


		// Controlling the constant after horizontal iterations

		float m = 0;
		for (int i = 0; i < w + 2; i++)
		for (int j = 0; j < h + 2; j++)
		{
			m += Inew(i, j);
		}
		m /= (w+2) * (h+2);

		for (int i = 0; i < w + 2; i++)
		for (int j = 0; j < h + 2; j++)
		{
			I(i, j) = Inew(i, j) - m;
		}

		delta /= (float)sqrt(w * h);
		float dpd = fabs((delta - delta_prev) / delta);

		// This formula is found experimentally
		float progress = (float)fmin(pow(stop_dpd / (dpd + 0.000001), 0.78), 0.999);

		// we use it cause progress bar should not move to the lowering direction
		if (progress_max < progress) progress_max = progress;

		if (!(*reporter)(reporterCallerData, progress_max))
		{
			return false;
		}

		printf("step #%d, progress: %f\n", step, progress);
		fflush(stdout);


		if (dpd < stop_dpd && step > 1)
		{
			break;
		}


		delta_prev = delta;
		delta = 0;
	}

	delete [] pnthrs;

	for (int i = 1; i < w + 1; i++)
	for (int j = 1; j < h + 1; j++)
	{
		I0(i - 1, j - 1) = I(i, j);
	}

	return true;
}


arr2<float> SolvePoissonNeimanMultiLattice(arr2<float> rho, int steps_max, float stop_dpd, progressReporter* reporter, void* reporterCallerData)
{
	DEBUG_INFO
	// Making lower resolutions
	int W = rho.getWidth();
	int H = rho.getHeight();

	vector<int> ww, hh;
	DEBUG_INFO

	int divides = 0, wt = W, ht = H;
	ww.push_back(wt); hh.push_back(ht);

	while (wt > 1 && ht > 1 && divides < 2)
	{
		wt /= 2; ht /= 2;
		ww.push_back(wt); hh.push_back(ht);
		divides ++;
	}

	vector<arr2<float> > Rho;
	Rho.push_back(rho);

	DEBUG_INFO
	for (int p = 1; p <= divides; p++)
	{
		int w = ww[p - 1];
		int h = hh[p - 1];

		arr2<float> rho_new(ww[p], hh[p]);
		for (int i = 0; i < w; i++)

		for (int j = 0; j < h; j++)
		{
			if ((i / 2 < ww[p]) && (j / 2 < hh[p]))
				rho_new(i / 2, j / 2) += 0.25f * Rho[p - 1](i, j);
		}

		Rho.push_back(rho_new);

		//(*reporter)(reporterCallerData, 0.5 * (p - 1) / (divides - 1));

	}
	DEBUG_INFO


	arr2<float> I(Rho[divides].getWidth(), Rho[divides].getHeight());

	float start = 0;
	float width0 = 1.0 / (divides + 1);

	// Calculating norm
	float norm = 0;
	for (int p = divides; p >= 0; p--)
	{
		norm += exp(divides - p) * width0;
	}


	for (int p = divides; p >= 0; p--)
	{
		DEBUG_INFO

		float width = exp(divides - p) * width0 / norm;

		innerProgressListenerData data((Compress_progressListenerData*)reporterCallerData, reporter, start, width);
		if (!SolvePoissonNeiman(I, Rho[p], steps_max, stop_dpd, &innerProgressReporter, &data))
		{
			return arr2<float>(0, 0);
		}

		start += width;

		if (p > 0)
		{
			I = Upsample2(I, ww[p - 1], hh[p - 1]);
		}

		//(*reporter)(reporterCallerData, 0.5 + 0.5 * (divides - p) / divides);

	}

	DEBUG_INFO

	(*reporter)(reporterCallerData, 1);

	return I;
}


bool Compress(PreciseBitmap bmp, double curve, double noise_gate, double pressure, double contrast, float epsilon, int steps_max, progressReporter* reporter, void* reporterCallerData)
{
	arr2<float> r_chan(bmp.r, bmp.width, bmp.height);
	arr2<float> g_chan(bmp.g, bmp.width, bmp.height);
	arr2<float> b_chan(bmp.b, bmp.width, bmp.height);

	DEBUG_INFO

	int Hw = bmp.width;
	int Hh = bmp.height;
	arr2<float> H(Hw, Hh);

	// Calculating logarithmic luminosity
	for (int i = 0; i < Hw; i++)
	for (int j = 0; j < Hh; j++)
	{
		double light = sqrt((r_chan(i, j) * r_chan(i, j) +
		                     g_chan(i, j) * g_chan(i, j) +
		                     b_chan(i, j) * b_chan(i, j)) / 3);

		H(i, j) = (float)(log(light + 0.00001));
	}

	(*reporter)(reporterCallerData, 0.01);	// reporting 1%

	DEBUG_INFO

	arr2<float> grad_H_x(Hw, Hh);
	arr2<float> grad_H_y(Hw, Hh);

	DEBUG_INFO

	// Calculating gradient of H
	for (int i = 1; i < Hw - 1; i++)
	for (int j = 1; j < Hh - 1; j++)
	{
		grad_H_x(i, j) = (float)(H(i + 1, j) - H(i, j));
		grad_H_y(i, j) = (float)(H(i, j + 1) - H(i, j));
	}

	(*reporter)(reporterCallerData, 0.02);	// reporting 2%

	DEBUG_INFO

	// Calculating Phi
	arr2<float> Phi = BuildPhi(H, 0.01 * pressure, contrast, 0.2 * noise_gate);

	DEBUG_INFO

	// Calculating G and div_G

	arr2<float> div_G(Hw, Hh);
	for (int i = 0; i < Hw - 1; i++)
	{
		for (int j = 0; j < Hh - 1; j++)
		{
			float G_x_ij = grad_H_x(i, j) * Phi(i, j);
			float G_y_ij = grad_H_y(i, j) * Phi(i, j);

			float G_x_ip1j = grad_H_x(i + 1, j) * Phi(i + 1, j);
			float G_y_ijp1 = grad_H_y(i, j + 1) * Phi(i, j + 1);

			div_G(i, j) = - (G_x_ij - G_x_ip1j + G_y_ij - G_y_ijp1);
		}

		// it should spend 8% to get 10% progress after it's finished
		(*reporter)(reporterCallerData, 0.02 + 0.08 * i / (Hw - 1));
	}

	// Preparing the compressor

	double a, b;
	if (curve > 0)
	{
		a = log(2);
		b = log(1.0 + pow(100, fabs(curve * 1.5)));
	}
	else
	{
		b = log(2);
		a = log(1.0 + pow(100, fabs(curve * 1.5)));
	}
	double p = pow(100, curve * 1.5);

	DEBUG_INFO

	// Solving Poisson equation Delta I = div G
	//arr2<float> I(Phi.getWidth(), Phi.getHeight());
	//SolvePoissonNeiman(I, div_G, steps_max, epsilon);

	innerProgressListenerData multiLatticeData((Compress_progressListenerData*)reporterCallerData, reporter, 0.1, 0.9);
	arr2<float> I = SolvePoissonNeimanMultiLattice(div_G, steps_max, epsilon, &innerProgressReporter, &multiLatticeData);
	if (I.getWidth() == 0 && I.getHeight() == 0)
	{
		// This case means that we got user cancel.
		return false;
	}

	DEBUG_INFO

	double kw = H.getWidth() / I.getWidth();
	double kh = H.getHeight() / I.getHeight();

	// Draw it
	for (int i = 0; i < bmp.width; i++)
	for (int j = 0; j < bmp.height; j++)
	{

		double Lold = exp(H(i, j));

		double Lcomp = log(p * (exp(a * Lold) - 1.0) + 1.0) / b;

		int i1 = (int)(fmin (i / kw, I.getWidth() - 1));
		int j1 = (int)(fmin (j / kh, I.getHeight() - 1));

		double L = exp(I(i1, j1)) * Lcomp;

		bmp.r[j * bmp.width + i] = (float)(bmp.r[j * bmp.width + i] * L / (Lold + 0.00001));
		bmp.g[j * bmp.width + i] = (float)(bmp.g[j * bmp.width + i] * L / (Lold + 0.00001));
		bmp.b[j * bmp.width + i] = (float)(bmp.b[j * bmp.width + i] * L / (Lold + 0.00001));
	}

	return true;
}

struct Compress_progressListenerData
{
	jobject& listener;
	JNIEnv& env;

	Compress_progressListenerData(jobject& listener, JNIEnv& env) : listener(listener), env(env) {}
};

bool Compress_progressReporter(void* callerData, float progress)
{
	DEBUG_INFO
	Compress_progressListenerData& data = *(Compress_progressListenerData*)callerData;
	jclass progressListener_cls = data.env.FindClass(JCLASS_PROGRESS_LISTENER);
	jmethodID reportProgress_mtd = data.env.GetMethodID(progressListener_cls, JCLASS_PROGRESS_LISTENER_REPORT_PROGRESS, JCLASS_PROGRESS_LISTENER_REPORT_PROGRESS_SIGNATURE);

	DEBUG_INFO
	// Reporting the progress
	jboolean res = data.env.CallBooleanMethod(data.listener, reportProgress_mtd, (jfloat)progress);

	DEBUG_INFO
	return (bool)res;
}

JNIEXPORT jboolean JNICALL Java_com_cateye_procedures_compressor_CompressorStageOperationProcessor_process
  (JNIEnv * env, jobject obj, jobject params, jobject bitmap, jobject listener)
{
	// Getting the class
	jclass cls = env->GetObjectClass(bitmap);

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	// Getting the bitmap from JVM
	PreciseBitmap bmp;
	bmp.r = (float*)env->GetLongField(bitmap, r_id);
	bmp.g = (float*)env->GetLongField(bitmap, g_id);
	bmp.b = (float*)env->GetLongField(bitmap, b_id);
	bmp.width = env->GetIntField(bitmap, width_id);
	bmp.height = env->GetIntField(bitmap, height_id);

	// Getting the stage operation parameters
	jclass operationClass = env->GetObjectClass(params);
	jfieldID curveId, noiseGateId, pressureId, contrastId;

	curveId = env->GetFieldID(operationClass, "curve", "D");
	noiseGateId = env->GetFieldID(operationClass, "noiseGate", "D");
	pressureId = env->GetFieldID(operationClass, "pressure", "D");
	contrastId = env->GetFieldID(operationClass, "contrast", "D");

	double curve, noiseGate, pressure, contrast;
	curve = (double)env->GetDoubleField(params, curveId);
	noiseGate = (double)env->GetDoubleField(params, noiseGateId);
	pressure = (double)env->GetDoubleField(params, pressureId);
	contrast = (double)env->GetDoubleField(params, contrastId);

/*	curve = 0.7;
	noiseGate = 0.1;
	pressure = 1;
	contrast = 0.8;*/

	time_t start;
	time(&start);

	Compress_progressListenerData data = Compress_progressListenerData(listener, *env);
	if (!Compress(bmp, curve, noiseGate, pressure, contrast, 0.003f, 10000, &Compress_progressReporter, &data))
	{
		printf("User canceled the operation!\n");	fflush(stdout);

		return false;
	}
	time_t end;
	time(&end);

	printf("time spent on compressing: %d seconds\n", end - start);
	fflush(stdout);

	return true;
}
