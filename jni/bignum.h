// Copyright (c) 2009-2010 Satoshi Nakamoto
// Copyright (c) 2011 The Bitcoin developers
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
#ifndef BITCOIN_BIGNUM_H
#define BITCOIN_BIGNUM_H

#include <stdexcept>
#include <vector>
#include <openssl/bn.h>

#include "BitcoinUtil.h"

class bignum_error : public std::runtime_error
{
public:
	explicit bignum_error(const std::string& str) : std::runtime_error(str) {}
};



class CAutoBN_CTX
{
private:
	CAutoBN_CTX(const CAutoBN_CTX&); // no implementation
	CAutoBN_CTX& operator=(const CAutoBN_CTX&); // no implementation

protected:
	BN_CTX* pctx;
	CAutoBN_CTX& operator=(BN_CTX* pnew) { pctx = pnew; return *this; }

public:
	CAutoBN_CTX()
	{
		pctx = BN_CTX_new();
		if (pctx == NULL)
			throw bignum_error("CAutoBN_CTX : BN_CTX_new() returned NULL");
	}

	~CAutoBN_CTX()
	{
		if (pctx != NULL)
			BN_CTX_free(pctx);
	}

	operator BN_CTX*() { return pctx; }
	BN_CTX& operator*() { return *pctx; }
	BN_CTX** operator&() { return &pctx; }
	bool operator!() { return (pctx == NULL); }
};



class CBigNum : public BIGNUM
{
public:
	CBigNum()
	{
		BN_init(this);
	}

	CBigNum(const CBigNum& b)
	{
		BN_init(this);
		if (!BN_copy(this, &b))
		{
			BN_clear_free(this);
			throw bignum_error("CBigNum::CBigNum(const CBigNum&) : BN_copy failed");
		}
	}

	CBigNum& operator=(const CBigNum& b)
	{
		if (!BN_copy(this, &b))
			throw bignum_error("CBigNum::operator= : BN_copy failed");
		return (*this);
	}

	~CBigNum()
	{
		BN_clear_free(this);
	}

	CBigNum(char n)			 { BN_init(this); if (n >= 0) setulong(n); else setint64(n); }
	CBigNum(short n)			{ BN_init(this); if (n >= 0) setulong(n); else setint64(n); }
	CBigNum(int n)			  { BN_init(this); if (n >= 0) setulong(n); else setint64(n); }
	CBigNum(long n)			 { BN_init(this); if (n >= 0) setulong(n); else setint64(n); }
	CBigNum(int64 n)			{ BN_init(this); setint64(n); }
	CBigNum(unsigned char n)	{ BN_init(this); setulong(n); }
	CBigNum(unsigned short n)   { BN_init(this); setulong(n); }
	CBigNum(unsigned int n)	 { BN_init(this); setulong(n); }
	CBigNum(uint64 n)		   { BN_init(this); setuint64(n); }
	explicit CBigNum(uint256 n) { BN_init(this); setuint256(n); }

	explicit CBigNum(const std::vector<unsigned char>& vch)
	{
		BN_init(this);
		setvch(vch);
	}

	void setuint(unsigned int n)
	{
		setulong(static_cast<unsigned long>(n));
	}

	unsigned int getuint() const
	{
		return BN_get_word(this);
	}

	int getint() const
	{
		unsigned long n = BN_get_word(this);
		if (!BN_is_negative(this))
			return (n > INT_MAX ? INT_MAX : n);
		else
			return (n > INT_MAX ? INT_MIN : -(int)n);
	}

	void setint64(int64 n)
	{
		unsigned char pch[sizeof(n) + 6];
		unsigned char* p = pch + 4;
		bool fNegative = false;
		if (n < (int64)0)
		{
			n = -n;
			fNegative = true;
		}
		bool fLeadingZeroes = true;
		for (int i = 0; i < 8; i++)
		{
			unsigned char c = (n >> 56) & 0xff;
			n <<= 8;
			if (fLeadingZeroes)
			{
				if (c == 0)
					continue;
				if (c & 0x80)
					*p++ = (fNegative ? 0x80 : 0);
				else if (fNegative)
					c |= 0x80;
				fLeadingZeroes = false;
			}
			*p++ = c;
		}
		unsigned int nSize = p - (pch + 4);
		pch[0] = (nSize >> 24) & 0xff;
		pch[1] = (nSize >> 16) & 0xff;
		pch[2] = (nSize >> 8) & 0xff;
		pch[3] = (nSize) & 0xff;
		BN_mpi2bn(pch, p - pch, this);
	}

	uint64 getuint64() const
	{
#if (ULONG_MAX > UINT_MAX)
		return static_cast<uint64>(getulong());
#else
		int len = BN_num_bytes(this);
		if (len > 8)
			throw std::runtime_error("BN getuint64 overflow");

		unsigned char buf[8];
		memset(buf, 0, sizeof(buf));
		BN_bn2bin(this, buf + 8 - len);
		return
			static_cast<uint64>(buf[0]) << 56 | static_cast<uint64>(buf[1]) << 48 |
			static_cast<uint64>(buf[2]) << 40 | static_cast<uint64>(buf[3]) << 32 |
			static_cast<uint64>(buf[4]) << 24 | static_cast<uint64>(buf[5]) << 16 |
			static_cast<uint64>(buf[6]) << 8 | static_cast<uint64>(buf[7]);
#endif
	}

	void setuint64(uint64 n)
	{
#if (ULONG_MAX > UINT_MAX)
		setulong(static_cast<unsigned long>(n));
#else
		unsigned char buf[8];
		buf[0] = static_cast<unsigned char>((n >> 56) & 0xff);
		buf[1] = static_cast<unsigned char>((n >> 48) & 0xff);
		buf[2] = static_cast<unsigned char>((n >> 40) & 0xff);
		buf[3] = static_cast<unsigned char>((n >> 32) & 0xff);
		buf[4] = static_cast<unsigned char>((n >> 24) & 0xff);
		buf[5] = static_cast<unsigned char>((n >> 16) & 0xff);
		buf[6] = static_cast<unsigned char>((n >> 8) & 0xff);
		buf[7] = static_cast<unsigned char>((n) & 0xff);
		BN_bin2bn(buf, 8, this);
#endif
	}

	void setuint256(const uint256& n)
	{
		BN_bin2bn(n.begin(), n.size(), NULL);
	}

	uint256 getuint256()
	{
		uint256 ret;
		unsigned int size = BN_num_bytes(this);
		if (size > ret.size())
			return ret;
		BN_bn2bin(this, ret.begin() + (ret.size() - BN_num_bytes(this)));
		return ret;
	}

	void setvch(const std::vector<unsigned char>& vch)
	{
		std::vector<unsigned char> vch2(vch.size() + 4);
		unsigned int nSize = vch.size();
		// BIGNUM's byte stream format expects 4 bytes of
		// big endian size data info at the front
		vch2[0] = (nSize >> 24) & 0xff;
		vch2[1] = (nSize >> 16) & 0xff;
		vch2[2] = (nSize >> 8) & 0xff;
		vch2[3] = (nSize >> 0) & 0xff;
		// swap data to big endian
		std::reverse_copy(vch.begin(), vch.end(), vch2.begin() + 4);
		BN_mpi2bn(&vch2[0], vch2.size(), this);
	}

	std::vector<unsigned char> getvch() const
	{
		unsigned int nSize = BN_bn2mpi(this, NULL);
		if (nSize < 4)
			return std::vector<unsigned char>();
		std::vector<unsigned char> vch(nSize);
		BN_bn2mpi(this, &vch[0]);
		vch.erase(vch.begin(), vch.begin() + 4);
		reverse(vch.begin(), vch.end());
		return vch;
	}

	CBigNum& SetCompact(unsigned int nCompact)
	{
		unsigned int nSize = nCompact >> 24;
		std::vector<unsigned char> vch(4 + nSize);
		vch[3] = nSize;
		if (nSize >= 1) vch[4] = (nCompact >> 16) & 0xff;
		if (nSize >= 2) vch[5] = (nCompact >> 8) & 0xff;
		if (nSize >= 3) vch[6] = (nCompact >> 0) & 0xff;
		BN_mpi2bn(&vch[0], vch.size(), this);
		return *this;
	}

	unsigned int GetCompact() const
	{
		unsigned int nSize = BN_bn2mpi(this, NULL);
		std::vector<unsigned char> vch(nSize);
		nSize -= 4;
		BN_bn2mpi(this, &vch[0]);
		unsigned int nCompact = nSize << 24;
		if (nSize >= 1) nCompact |= (vch[4] << 16);
		if (nSize >= 2) nCompact |= (vch[5] << 8);
		if (nSize >= 3) nCompact |= (vch[6] << 0);
		return nCompact;
	}

	void SetHex(const std::string& str)
	{
		// skip 0x
		const char* psz = str.c_str();
		while (isspace(*psz))
			psz++;
		bool fNegative = false;
		if (*psz == '-')
		{
			fNegative = true;
			psz++;
		}
		if (psz[0] == '0' && tolower(psz[1]) == 'x')
			psz += 2;
		while (isspace(*psz))
			psz++;

		// hex string to bignum
		static char phexdigit[256] = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,1,2,3,4,5,6,7,8,9,0,0,0,0,0,0, 0,0xa,0xb,0xc,0xd,0xe,0xf,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0xa,0xb,0xc,0xd,0xe,0xf,0,0,0,0,0,0,0,0,0 };
		*this = 0;
		while (isxdigit(*psz))
		{
			*this <<= 4;
			int n = phexdigit[(int) *psz++];
			*this += n;
		}
		if (fNegative)
			*this = 0 - *this;
	}

	std::string ToString(int nBase=10) const
	{
		CAutoBN_CTX pctx;
		CBigNum bnBase = nBase;
		CBigNum bn0 = 0;
		std::string str;
		CBigNum bn = *this;
		BN_set_negative(&bn, false);
		CBigNum dv;
		CBigNum rem;
		if (BN_cmp(&bn, &bn0) == 0)
			return "0";
		while (BN_cmp(&bn, &bn0) > 0)
		{
			if (!BN_div(&dv, &rem, &bn, &bnBase, pctx))
				throw bignum_error("CBigNum::ToString() : BN_div failed");
			bn = dv;
			unsigned int c = rem.getuint();
			str += "0123456789abcdef"[c];
		}
		if (BN_is_negative(this))
			str += "-";
		reverse(str.begin(), str.end());
		return str;
	}

	std::string GetHex() const
	{
		return ToString(16);
	}
	/* JED
	unsigned int GetSerializeSize(int nType=0, int nVersion=VERSION) const
	{
		return ::GetSerializeSize(getvch(), nType, nVersion);
	}

	template<typename Stream>
	void Serialize(Stream& s, int nType=0, int nVersion=VERSION) const
	{
		::Serialize(s, getvch(), nType, nVersion);
	}

	template<typename Stream>
	void Unserialize(Stream& s, int nType=0, int nVersion=VERSION)
	{
		std::vector<unsigned char> vch;
		::Unserialize(s, vch, nType, nVersion);
		setvch(vch);
	}*/


	bool operator!() const
	{
		return BN_is_zero(this);
	}

	CBigNum& operator+=(const CBigNum& b)
	{
		if (!BN_add(this, this, &b))
			throw bignum_error("CBigNum::operator+= : BN_add failed");
		return *this;
	}

	CBigNum& operator-=(const CBigNum& b)
	{
		*this = *this - b;
		return *this;
	}

	CBigNum& operator*=(const CBigNum& b)
	{
		CAutoBN_CTX pctx;
		if (!BN_mul(this, this, &b, pctx))
			throw bignum_error("CBigNum::operator*= : BN_mul failed");
		return *this;
	}

	CBigNum& operator/=(const CBigNum& b)
	{
		*this = *this / b;
		return *this;
	}

	CBigNum& operator%=(const CBigNum& b)
	{
		*this = *this % b;
		return *this;
	}

	CBigNum& operator<<=(unsigned int shift)
	{
		if (!BN_lshift(this, this, shift))
			throw bignum_error("CBigNum:operator<<= : BN_lshift failed");
		return *this;
	}

	CBigNum& operator>>=(unsigned int shift)
	{
		// Note: BN_rshift segfaults on 64-bit if 2^shift is greater than the number
		//   if built on ubuntu 9.04 or 9.10, probably depends on version of openssl
		CBigNum a = 1;
		a <<= shift;
		if (BN_cmp(&a, this) > 0)
		{
			*this = 0;
			return *this;
		}

		if (!BN_rshift(this, this, shift))
			throw bignum_error("CBigNum:operator>>= : BN_rshift failed");
		return *this;
	}


	CBigNum& operator++()
	{
		// prefix operator
		if (!BN_add(this, this, BN_value_one()))
			throw bignum_error("CBigNum::operator++ : BN_add failed");
		return *this;
	}

	const CBigNum operator++(int)
	{
		// postfix operator
		const CBigNum ret = *this;
		++(*this);
		return ret;
	}

	CBigNum& operator--()
	{
		// prefix operator
		CBigNum r;
		if (!BN_sub(&r, this, BN_value_one()))
			throw bignum_error("CBigNum::operator-- : BN_sub failed");
		*this = r;
		return *this;
	}

	const CBigNum operator--(int)
	{
		// postfix operator
		const CBigNum ret = *this;
		--(*this);
		return ret;
	}


	friend inline const CBigNum operator-(const CBigNum& a, const CBigNum& b);
	friend inline const CBigNum operator/(const CBigNum& a, const CBigNum& b);
	friend inline const CBigNum operator%(const CBigNum& a, const CBigNum& b);

	private:

	// private because the size of an unsigned long varies by platform

	void setulong(unsigned long n)
	{
		if (!BN_set_word(this, n))
			throw bignum_error("CBigNum conversion from unsigned long : BN_set_word failed");
	}

	unsigned long getulong() const
	{
		return BN_get_word(this);
	}

};



inline const CBigNum operator+(const CBigNum& a, const CBigNum& b)
{
	CBigNum r;
	if (!BN_add(&r, &a, &b))
		throw bignum_error("CBigNum::operator+ : BN_add failed");
	return r;
}

inline const CBigNum operator-(const CBigNum& a, const CBigNum& b)
{
	CBigNum r;
	if (!BN_sub(&r, &a, &b))
		throw bignum_error("CBigNum::operator- : BN_sub failed");
	return r;
}

inline const CBigNum operator-(const CBigNum& a)
{
	CBigNum r(a);
	BN_set_negative(&r, !BN_is_negative(&r));
	return r;
}

inline const CBigNum operator*(const CBigNum& a, const CBigNum& b)
{
	CAutoBN_CTX pctx;
	CBigNum r;
	if (!BN_mul(&r, &a, &b, pctx))
		throw bignum_error("CBigNum::operator* : BN_mul failed");
	return r;
}

inline const CBigNum operator/(const CBigNum& a, const CBigNum& b)
{
	CAutoBN_CTX pctx;
	CBigNum r;
	if (!BN_div(&r, NULL, &a, &b, pctx))
		throw bignum_error("CBigNum::operator/ : BN_div failed");
	return r;
}

inline const CBigNum operator%(const CBigNum& a, const CBigNum& b)
{
	CAutoBN_CTX pctx;
	CBigNum r;
	if (!BN_mod(&r, &a, &b, pctx))
		throw bignum_error("CBigNum::operator% : BN_div failed");
	return r;
}

inline const CBigNum operator<<(const CBigNum& a, unsigned int shift)
{
	CBigNum r;
	if (!BN_lshift(&r, &a, shift))
		throw bignum_error("CBigNum:operator<< : BN_lshift failed");
	return r;
}

inline const CBigNum operator>>(const CBigNum& a, unsigned int shift)
{
	CBigNum r = a;
	r >>= shift;
	return r;
}

inline bool operator==(const CBigNum& a, const CBigNum& b) { return (BN_cmp(&a, &b) == 0); }
inline bool operator!=(const CBigNum& a, const CBigNum& b) { return (BN_cmp(&a, &b) != 0); }
inline bool operator<=(const CBigNum& a, const CBigNum& b) { return (BN_cmp(&a, &b) <= 0); }
inline bool operator>=(const CBigNum& a, const CBigNum& b) { return (BN_cmp(&a, &b) >= 0); }
inline bool operator<(const CBigNum& a, const CBigNum& b)  { return (BN_cmp(&a, &b) < 0); }
inline bool operator>(const CBigNum& a, const CBigNum& b)  { return (BN_cmp(&a, &b) > 0); }

#endif

// vim:ts=4
